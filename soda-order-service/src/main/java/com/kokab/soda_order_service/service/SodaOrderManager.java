package com.kokab.soda_order_service.service;

import bottling.model.SodaOrderDto;
import com.kokab.soda_order_service.domain.SodaOrder;

import java.util.UUID;

public interface SodaOrderManager {

    SodaOrder newSodaOrder(SodaOrder sodaOrder);

    void processValidationResult(UUID sodaOrderId, Boolean isValid);

    void sodaOrderAllocationPassed(SodaOrderDto sodaOrder);

    void sodaOrderAllocationPendingInventory(SodaOrderDto sodaOrder);

    void sodaOrderAllocationFailed(SodaOrderDto sodaOrder);

    void sodaOrderPickedUp(UUID id);

    void cancelOrder(UUID id);
}

