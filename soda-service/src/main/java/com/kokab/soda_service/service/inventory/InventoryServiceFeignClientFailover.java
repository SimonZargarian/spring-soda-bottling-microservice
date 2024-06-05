package com.kokab.soda_service.service.inventory;

import com.kokab.soda_service.service.inventory.model.SodaInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@RequiredArgsConstructor
@Component
public class InventoryServiceFeignClientFailover implements InventoryServiceFeignClient {

    private final InventoryFailoverFeignClient failoverFeignClient;

    @Override
    public ResponseEntity<List<SodaInventoryDto>> getOnhandInventory(UUID beerId) {
        return failoverFeignClient.getOnHandInventory();
    }
}
