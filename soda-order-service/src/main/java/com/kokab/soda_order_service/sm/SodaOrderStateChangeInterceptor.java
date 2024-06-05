package com.kokab.soda_order_service.sm;

import com.kokab.soda_order_service.domain.SodaOrder;
import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.repository.SodaOrderRepository;
import com.kokab.soda_order_service.service.SodaOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SodaOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;

    @Transactional
    //@Override
    public void preStateChange(State<SodaOrderStatusEnum, SodaOrderEventEnum> state, Message<SodaOrderEventEnum> message, Transition<SodaOrderStatusEnum, SodaOrderEventEnum> transition, StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> stateMachine) {
        log.debug("Pre-State Change");

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(SodaOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

                    SodaOrder sodaOrder = sodaOrderRepository.getOne(UUID.fromString(orderId));
                    sodaOrder.setOrderStatus(state.getId());
                    sodaOrderRepository.saveAndFlush(sodaOrder);
                });
    }
}
