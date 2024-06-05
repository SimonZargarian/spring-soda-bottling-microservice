package com.kokab.soda_order_service.web.controller;

import bottling.model.SodaOrderDto;
import bottling.model.SodaOrderPagedList;
import com.kokab.soda_order_service.service.SodaOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class SodaOrderController {
    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final SodaOrderService sodaOrderService;

    public SodaOrderController(SodaOrderService sodaOrderService) {
        this.sodaOrderService = sodaOrderService;
    }

    @GetMapping("orders")
    public SodaOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                         @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize){

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return sodaOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public SodaOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody SodaOrderDto sodaOrderDto){
        return sodaOrderService.placeOrder(customerId, sodaOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public SodaOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        return sodaOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId){
        sodaOrderService.pickupOrder(customerId, orderId);
    }
}

