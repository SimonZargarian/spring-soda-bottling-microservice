package com.kokab.soda_service.web.mapper;

import bottling.model.SodaDto;
import com.kokab.soda_service.domain.Soda;
import com.kokab.soda_service.service.inventory.SodaInventoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SodaMapperDecorator implements SodaMapper {
    private SodaInventoryService sodaInventoryService;
    private SodaMapper mapper;

    @Autowired
    public void setSodaInventoryService(SodaInventoryService sodaInventoryService) {
        this.sodaInventoryService = sodaInventoryService;
    }

    @Autowired
    public void setMapper(SodaMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SodaDto sodaToSodaDto(Soda soda) {
        return mapper.sodaToSodaDto(soda);
    }

    @Override
    public SodaDto sodaToSodaDtoWithInventory(Soda soda) {
        SodaDto dto = mapper.sodaToSodaDto(soda);
        dto.setQuantityOnHand(sodaInventoryService.getOnHandInventory(soda.getId()));
        return dto;
    }

    @Override
    public Soda sodaDtoToSoda(SodaDto sodaDto) {
        return mapper.sodaDtoToSoda(sodaDto);
    }
}
