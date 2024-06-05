package com.kokab.soda_service.web.mapper;

import bottling.model.SodaDto;
import com.kokab.soda_service.domain.Soda;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(SodaMapperDecorator.class)
public interface SodaMapper {

    SodaDto sodaToSodaDto(Soda soda);

    SodaDto sodaToSodaDtoWithInventory(Soda soda);

    Soda sodaDtoToSoda(SodaDto dto);


}
