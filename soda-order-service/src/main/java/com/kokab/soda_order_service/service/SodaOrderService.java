package com.kokab.soda_order_service.service;

import bottling.model.SodaOrderDto;
import bottling.model.SodaOrderPagedList;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SodaOrderService {

    SodaOrderPagedList listOrders(UUID customerId, Pageable pageable);

    SodaOrderDto placeOrder(UUID customerId, SodaOrderDto sodaOrderDto);

    SodaOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
