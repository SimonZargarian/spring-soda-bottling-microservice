package com.kokab.soda_service.bootstrap;

import bottling.model.SodaStyleEnum;
import com.kokab.soda_service.domain.Soda;
import com.kokab.soda_service.repository.SodaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@RequiredArgsConstructor
@Component
public class SodaLoader implements CommandLineRunner  {

    public static final String BEER_1_UPC = "0631234200036";
    public static final String BEER_2_UPC = "0631234300019";
    public static final String BEER_3_UPC = "0083783375213";

    private final SodaRepository sodaRepository;

    @Override
    public void run(String... args) throws Exception {

        if(sodaRepository.count() == 0 ) {
            loadBeerObjects();
        }
    }

    private void loadBeerObjects() {
        Soda b1 = Soda.builder()
                .sodaName("Mango Bobs")
                .sodaStyle(SodaStyleEnum.COCO_COLA_ZERO.name())
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_1_UPC)
                .build();
        Soda b2 = Soda.builder()
                .sodaName("Galaxy Cat")
                .sodaStyle(SodaStyleEnum.DIET_COKE.name())
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_2_UPC)
                .build();

        Soda b3 = Soda.builder()
                .sodaName("Pinball Porter")
                .sodaStyle(SodaStyleEnum.DIET_COKE.name())
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_3_UPC)
                .build();

        sodaRepository.save(b1);
        sodaRepository.save(b2);
        sodaRepository.save(b3);
    }
}
