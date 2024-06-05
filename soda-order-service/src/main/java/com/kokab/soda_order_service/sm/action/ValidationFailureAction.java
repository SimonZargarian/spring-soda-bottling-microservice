package com.kokab.soda_order_service.sm.action;

import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.service.SodaOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationFailureAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> context) {
        String sodaOrderId = (String) context.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compensating Transaction.... Validation Failed: " + sodaOrderId);
    }
}