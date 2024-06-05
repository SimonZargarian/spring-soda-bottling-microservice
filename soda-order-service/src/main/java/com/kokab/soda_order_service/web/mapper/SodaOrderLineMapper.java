package com.kokab.soda_order_service.web.mapper;

import bottling.model.SodaOrderLineDto;
import com.kokab.soda_order_service.domain.SodaOrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(SodaOrderLineMapperDecorator.class)
public interface SodaOrderLineMapper {
    SodaOrderLineDto sodaOrderLineToDto(SodaOrderLine line);

    SodaOrderLine dtoToSodaOrderLine(SodaOrderLineDto dto);
}
