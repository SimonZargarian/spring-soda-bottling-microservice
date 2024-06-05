package com.kokab.soda_service.service.order;

import bottling.model.events.SodaOrderDto;
import com.kokab.soda_service.repository.SodaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Component
public class SodaOrderValidator {

    private final SodaRepository beerRepository;

    public Boolean validateOrder(SodaOrderDto sodaOrder){

        AtomicInteger beersNotFound = new AtomicInteger();

        sodaOrder.getSodaOrderLines().forEach(orderline -> {
            if(beerRepository.findByUpc(orderline.getUpc()) == null){
                beersNotFound.incrementAndGet();
            }
        });

        return beersNotFound.get() == 0;
    }
}
