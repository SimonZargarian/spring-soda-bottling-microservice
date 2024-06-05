package com.kokab.soda_order_service.web.mapper;

import bottling.model.CustomerDto;
import com.kokab.soda_order_service.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}
