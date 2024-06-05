package com.kokab.soda_order_service.service.listener;

import bottling.model.event.ValidateOrderResult;
import com.kokab.soda_order_service.config.JmsConfig;
import com.kokab.soda_order_service.service.SodaOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final SodaOrderManager sodaOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){
        final UUID sodaOrderId = result.getOrderId();

        log.debug("Validation Result for Order Id: " + sodaOrderId);

        sodaOrderManager.processValidationResult(sodaOrderId, result.getIsValid());
    }
}
