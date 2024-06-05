package com.kokab.soda_service.service.bottling;

import bottling.model.SodaDto;
import bottling.model.events.BottlingSodaEvent;
import bottling.model.events.NewInventoryEvent;
import com.kokab.soda_service.config.JmsConfig;
import com.kokab.soda_service.domain.Soda;
import com.kokab.soda_service.repository.SodaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BottlingSodaListener {

    private final SodaRepository beerRepository;
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.BOTTLING_REQUEST_QUEUE)
    public void listen(BottlingSodaEvent event){
        SodaDto sodaDto = event.getBeerDto();

        Soda soda = beerRepository.getOne(sodaDto.getId());

        sodaDto.setQuantityOnHand(soda.getQuantityToBrew());

        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(sodaDto);

        log.debug("Brewed beer " + soda.getMinOnHand() + " : QOH: " + sodaDto.getQuantityOnHand());

        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE, newInventoryEvent);
    }
}
