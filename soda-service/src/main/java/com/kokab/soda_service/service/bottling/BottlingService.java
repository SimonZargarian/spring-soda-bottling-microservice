package com.kokab.soda_service.service.bottling;

import bottling.model.events.BottlingSodaEvent;
import com.kokab.soda_service.config.JmsConfig;
import com.kokab.soda_service.domain.Soda;
import com.kokab.soda_service.repository.SodaRepository;
import com.kokab.soda_service.service.inventory.SodaInventoryService;
import com.kokab.soda_service.web.mapper.SodaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BottlingService {
    private final SodaRepository sodaRepository;
    private final SodaInventoryService sodaInventoryService;
    private final JmsTemplate jmsTemplate;
    private final SodaMapper sodaMapper;

    @Scheduled(fixedRate = 5000) //every 5 seconds
    public void checkForLowInventory(){
        List<Soda> beers = sodaRepository.findAll();

        beers.forEach(soda -> {
            Integer invQOH = sodaInventoryService.getOnHandInventory(soda.getId());
            log.debug("Checking Inventory for: " + soda.getSodaName() + " / " + soda.getId());
            log.debug("Min Onhand is: " + soda.getMinOnHand());
            log.debug("Inventory is: "  + invQOH);

            if(soda.getMinOnHand() >= invQOH){
                jmsTemplate.convertAndSend(JmsConfig.BOTTLING_REQUEST_QUEUE, new BottlingSodaEvent(sodaMapper.sodaToSodaDto(soda)));
            }
        });

    }
}
