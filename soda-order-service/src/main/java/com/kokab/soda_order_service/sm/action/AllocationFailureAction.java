package com.kokab.soda_order_service.sm.action;

import bottling.model.event.AllocationFailureEvent;
import com.kokab.soda_order_service.config.JmsConfig;
import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.service.SodaOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> context) {
        String sodaOrderId = (String) context.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, AllocationFailureEvent.builder()
                .orderId(UUID.fromString(sodaOrderId))
                .build());

        log.debug("Sent Allocation Failure Message to queue for order id " + sodaOrderId);
    }
}
