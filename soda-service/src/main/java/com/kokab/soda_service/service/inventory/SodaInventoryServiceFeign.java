package com.kokab.soda_service.service.inventory;

import com.kokab.soda_service.service.inventory.model.SodaInventoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
@Slf4j
@RequiredArgsConstructor
@Profile("local-discovery")
@Service
public class SodaInventoryServiceFeign implements SodaInventoryService{

    private final InventoryServiceFeignClient inventoryServiceFeignClient;

    @Override
    public Integer getOnHandInventory(UUID beerId) {
        log.debug("Calling Inventory Service - BeerId: " + beerId);

        ResponseEntity<List<SodaInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandInventory(beerId);

        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(SodaInventoryDto::getQuantityOnHand)
                .sum();

        log.debug("BeerId: " + beerId + " On hand is: " + onHand);

        return onHand;
    }
}
