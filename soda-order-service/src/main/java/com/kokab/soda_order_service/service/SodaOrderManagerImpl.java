package com.kokab.soda_order_service.service;

import bottling.model.SodaOrderDto;
import com.kokab.soda_order_service.domain.SodaOrder;
import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.repository.SodaOrderRepository;
import com.kokab.soda_order_service.sm.SodaOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class SodaOrderManagerImpl  implements SodaOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    private final StateMachineFactory<SodaOrderStatusEnum, SodaOrderEventEnum> stateMachineFactory;
    private final SodaOrderRepository sodaOrderRepository;
    private final SodaOrderStateChangeInterceptor sodaOrderStateChangeInterceptor;

    @Transactional
    @Override
    public SodaOrder newSodaOrder(SodaOrder sodaOrder) {
        sodaOrder.setId(null);
        sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);

        SodaOrder savedSodaOrder = sodaOrderRepository.saveAndFlush(sodaOrder);
        sendSodaOrderEvent(savedSodaOrder, SodaOrderEventEnum.VALIDATE_ORDER);
        return savedSodaOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID sodaOrderId, Boolean isValid) {
        log.debug("Process Validation Result for sodaOrderId: " + sodaOrderId + " Valid? " + isValid);

        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderId);

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            if(isValid){
                sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_PASSED);

                //wait for status change
                awaitForStatus(sodaOrderId, SodaOrderStatusEnum.VALIDATED);

                SodaOrder validatedOrder = sodaOrderRepository.findById(sodaOrderId).get();

                sendSodaOrderEvent(validatedOrder, SodaOrderEventEnum.ALLOCATE_ORDER);

            } else {
                sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error("Order Not Found. Id: " + sodaOrderId));
    }

    @Override
    public void sodaOrderAllocationPassed(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(sodaOrder.getId(), SodaOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(sodaOrderDto);
        }, () -> log.error("Order Id Not Found: " + sodaOrderDto.getId() ));
    }

    @Override
    public void sodaOrderAllocationPendingInventory(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(sodaOrder.getId(), SodaOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQty(sodaOrderDto);
        }, () -> log.error("Order Id Not Found: " + sodaOrderDto.getId() ));

    }

    private void updateAllocatedQty(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> allocatedOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getSodaOrderLines().forEach(sodaOrderLine -> {
                sodaOrderDto.getSodaOrderLines().forEach(sodaOrderLineDto -> {
                    if(sodaOrderLine.getId() .equals(sodaOrderLineDto.getId())){
                        sodaOrderLine.setQuantityAllocated(sodaOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            sodaOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + sodaOrderDto.getId()));
    }

    @Override
    public void sodaOrderAllocationFailed(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + sodaOrderDto.getId()) );

    }

    @Override
    public void sodaOrderPickedUp(UUID id) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(id);

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            //do process
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.SODA_ORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    @Override
    public void cancelOrder(UUID id) {
        sodaOrderRepository.findById(id).ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    private void sendSodaOrderEvent(SodaOrder sodaOrder, SodaOrderEventEnum eventEnum){
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = build(sodaOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, sodaOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private void awaitForStatus(UUID sodaOrderId, SodaOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            sodaOrderRepository.findById(sodaOrderId).ifPresentOrElse(sodaOrder -> {
                if (sodaOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + sodaOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    private StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> build(SodaOrder sodaOrder){
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = stateMachineFactory.getStateMachine(sodaOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(sodaOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(sodaOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
