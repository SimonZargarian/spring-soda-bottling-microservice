package com.kokab.soda_order_service.sm;

import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
@Slf4j
@Component
@RequiredArgsConstructor
public class SodaOrderStateMachineConfig  extends StateMachineConfigurerAdapter<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final Action<SodaOrderStatusEnum, SodaOrderEventEnum>  validateOrderAction;
    private final Action<SodaOrderStatusEnum, SodaOrderEventEnum>  allocateOrderAction;
    private final Action<SodaOrderStatusEnum, SodaOrderEventEnum>  validationFailureAction;
    private final Action<SodaOrderStatusEnum, SodaOrderEventEnum>  allocationFailureAction;
    private final Action<SodaOrderStatusEnum, SodaOrderEventEnum>  deallocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<SodaOrderStatusEnum, SodaOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(SodaOrderStatusEnum.NEW)
                .states(EnumSet.allOf(SodaOrderStatusEnum.class))
                .end(SodaOrderStatusEnum.PICKED_UP)
                .end(SodaOrderStatusEnum.DELIVERED)
                .end(SodaOrderStatusEnum.CANCELLED)
                .end(SodaOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(SodaOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(SodaOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SodaOrderStatusEnum, SodaOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(SodaOrderStatusEnum.NEW).target(SodaOrderStatusEnum.VALIDATION_PENDING)
                .event(SodaOrderEventEnum.VALIDATE_ORDER)
                .action(validateOrderAction)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATION_PENDING).target(SodaOrderStatusEnum.VALIDATED)
                .event(SodaOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATION_PENDING).target(SodaOrderStatusEnum.CANCELLED)
                .event(SodaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATION_PENDING).target(SodaOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(SodaOrderEventEnum.VALIDATION_FAILED)
                .action(validationFailureAction)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATED).target(SodaOrderStatusEnum.ALLOCATION_PENDING)
                .event(SodaOrderEventEnum.ALLOCATE_ORDER)
                .action(allocateOrderAction)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATED).target(SodaOrderStatusEnum.CANCELLED)
                .event(SodaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATION_PENDING).target(SodaOrderStatusEnum.ALLOCATED)
                .event(SodaOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATION_PENDING).target(SodaOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(SodaOrderEventEnum.ALLOCATION_FAILED)
                .action(allocationFailureAction)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATION_PENDING).target(SodaOrderStatusEnum.CANCELLED)
                .event(SodaOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATION_PENDING).target(SodaOrderStatusEnum.PENDING_INVENTORY)
                .event(SodaOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATED).target(SodaOrderStatusEnum.PICKED_UP)
                .event(SodaOrderEventEnum.SODA_ORDER_PICKED_UP)
                .and().withExternal()
                .source(SodaOrderStatusEnum.ALLOCATED).target(SodaOrderStatusEnum.CANCELLED)
                .event(SodaOrderEventEnum.CANCEL_ORDER)
                .action(deallocateOrderAction);
    }
}