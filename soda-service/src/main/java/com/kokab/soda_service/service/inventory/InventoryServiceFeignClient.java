package com.kokab.soda_service.service.inventory;

import com.kokab.soda_service.config.FeignClientConfig;
import com.kokab.soda_service.service.inventory.model.SodaInventoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;
@FeignClient(name = "inventory-service", fallback = InventoryServiceFeignClientFailover.class, configuration = FeignClientConfig.class)
public interface InventoryServiceFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = SodaInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<SodaInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}