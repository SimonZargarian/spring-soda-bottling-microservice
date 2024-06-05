package com.kokab.soda_inventory_service.service;

import bottling.model.SodaOrderDto;

public interface AllocationService {
    Boolean allocateOrder(SodaOrderDto sodaOrderDto);

    void deallocateOrder(SodaOrderDto sodaOrderDto);
}
