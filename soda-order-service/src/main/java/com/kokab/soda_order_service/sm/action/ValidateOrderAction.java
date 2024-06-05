package com.kokab.soda_order_service.sm.action;

import bottling.model.event.ValidateOrderRequest;
import com.kokab.soda_order_service.config.JmsConfig;
import com.kokab.soda_order_service.domain.SodaOrder;
import com.kokab.soda_order_service.domain.SodaOrderEventEnum;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.repository.SodaOrderRepository;
import com.kokab.soda_order_service.service.SodaOrderManagerImpl;
import com.kokab.soda_order_service.web.mapper.SodaOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;
    private final SodaOrderMapper sodaOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> context) {
        String sodaOrderId = (String) context.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(UUID.fromString(sodaOrderId));

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                    .sodaOrder(sodaOrderMapper.sodaOrderToDto(sodaOrder))
                    .build());
        }, () -> log.error("Order Not Found. Id: " + sodaOrderId));

        log.debug("Sent Validation request to queue for order id " + sodaOrderId);
    }
}
