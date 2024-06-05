package com.kokab.soda_inventory_service.web.controller;

import bottling.model.SodaInventoryDto;
import com.kokab.soda_inventory_service.repository.SodaInventoryRepository;
import com.kokab.soda_inventory_service.web.mapper.SodaInventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SodaInventoryController {

    private final SodaInventoryRepository sodaInventoryRepository;
    private final SodaInventoryMapper sodaInventoryMapper;

    @GetMapping("api/v1/soda/{sodaId}/inventory")
    List<SodaInventoryDto> listSodasById(@PathVariable UUID sodaId){
        log.debug("Finding Inventory for sodaId:" + sodaId);

        return sodaInventoryRepository.findAllBySodaId(sodaId)
                .stream()
                .map(sodaInventoryMapper::sodaInventoryToSodaInventoryDto)
                .collect(Collectors.toList());
    }
}
