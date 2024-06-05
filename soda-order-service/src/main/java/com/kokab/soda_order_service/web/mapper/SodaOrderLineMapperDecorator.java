package com.kokab.soda_order_service.web.mapper;

import bottling.model.SodaDto;
import bottling.model.SodaOrderLineDto;
import com.kokab.soda_order_service.domain.SodaOrderLine;
import com.kokab.soda_order_service.service.soda.SodaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;
public class SodaOrderLineMapperDecorator implements SodaOrderLineMapper {

    private SodaService sodaService;
    private SodaOrderLineMapper sodaOrderLineMapper;

    @Autowired
    public void setSodaService(SodaService sodaService) {
        this.sodaService = sodaService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setSodaOrderLineMapper(SodaOrderLineMapper sodaOrderLineMapper) {
        this.sodaOrderLineMapper = sodaOrderLineMapper;
    }

    @Override
    public SodaOrderLineDto sodaOrderLineToDto(SodaOrderLine line) {
        SodaOrderLineDto orderLineDto = sodaOrderLineMapper.sodaOrderLineToDto(line);
        Optional<SodaDto> sodaDtoOptional = sodaService.getSodaByUpc(line.getUpc());

        sodaDtoOptional.ifPresent(sodaDto -> {
            orderLineDto.setSodaName(sodaDto.getSodaName());
            orderLineDto.setSodaStyle(sodaDto.getSodaStyle());
            orderLineDto.setPrice(sodaDto.getPrice());
            orderLineDto.setSodaId(sodaDto.getId());
        });

        return orderLineDto;
    }

    @Override
    public SodaOrderLine dtoToSodaOrderLine(SodaOrderLineDto dto) {
        return null;
    }
}
