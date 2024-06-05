package com.kokab.soda_order_service.service.soda;

import bottling.model.SodaDto;

import java.util.Optional;
import java.util.UUID;
public interface SodaService {
    Optional<SodaDto> getSodaById(UUID uuid);

    Optional<SodaDto> getSodaByUpc(String upc);
}
