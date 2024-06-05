package com.kokab.soda_inventory_service.service;

import bottling.model.event.NewInventoryEvent;
import com.kokab.soda_inventory_service.config.JmsConfig;
import com.kokab.soda_inventory_service.domain.SodaInventory;
import com.kokab.soda_inventory_service.repository.SodaInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NewInventoryListener {

    private final SodaInventoryRepository sodaInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event){

        log.debug("Got Inventory: " + event.toString());

        sodaInventoryRepository.save(SodaInventory.builder()
                .sodaId(event.getSodaDto().getId())
                .upc(event.getSodaDto().getUpc())
                .quantityOnHand(event.getSodaDto().getQuantityOnHand())
                .build());
    }

}