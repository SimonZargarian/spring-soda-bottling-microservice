package com.kokab.soda_order_service.web.mapper;

import bottling.model.SodaOrderDto;
import com.kokab.soda_order_service.domain.SodaOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, SodaOrderLineMapper.class})
public interface SodaOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    SodaOrderDto sodaOrderToDto(SodaOrder sodaOrder);

    SodaOrder dtoToSodaOrder(SodaOrderDto dto);
}
