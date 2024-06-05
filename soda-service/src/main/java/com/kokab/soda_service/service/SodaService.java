package com.kokab.soda_service.service;

import bottling.model.SodaDto;
import bottling.model.SodaPagedList;
import bottling.model.SodaStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface SodaService {
    SodaPagedList listSodas(String sodaName, SodaStyleEnum sodaStyle, PageRequest pageRequest, Boolean showInventoryOnHand);

    SodaDto getById(UUID sodaId, Boolean showInventoryOnHand);

    SodaDto saveNewSoda(SodaDto sodaDto);

    SodaDto updateSoda(UUID beerId, SodaDto sodaDto);

    SodaDto getByUpc(String upc);
}
