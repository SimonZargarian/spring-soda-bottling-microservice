package com.kokab.soda_order_service.service.listener;

import bottling.model.event.AllocateOrderResult;
import com.kokab.soda_order_service.config.JmsConfig;
import com.kokab.soda_order_service.service.SodaOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SodaOrderAllocationResultListener {
    private final SodaOrderManager sodaOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            sodaOrderManager.sodaOrderAllocationPassed(result.getSodaOrderDto());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            sodaOrderManager.sodaOrderAllocationPendingInventory(result.getSodaOrderDto());
        } else if(result.getAllocationError()){
            //allocation error
            sodaOrderManager.sodaOrderAllocationFailed(result.getSodaOrderDto());
        }
    }
}
