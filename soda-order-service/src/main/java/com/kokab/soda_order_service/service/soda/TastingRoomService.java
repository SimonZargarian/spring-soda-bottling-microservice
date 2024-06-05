package com.kokab.soda_order_service.service.soda;

import bottling.model.SodaOrderDto;
import bottling.model.SodaOrderLineDto;
import com.kokab.soda_order_service.bootstrap.SodaOrderBootStrap;
import com.kokab.soda_order_service.domain.Customer;
import com.kokab.soda_order_service.repository.CustomerRepository;
import com.kokab.soda_order_service.repository.SodaOrderRepository;
import com.kokab.soda_order_service.service.SodaOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class TastingRoomService {

    private final CustomerRepository customerRepository;
    private final SodaOrderService sodaOrderService;
    private final SodaOrderRepository sodaOrderRepository;
    private final List<String> sodaUpcs = new ArrayList<>(3);

    public TastingRoomService(CustomerRepository customerRepository, SodaOrderService sodaOrderService,
                              SodaOrderRepository sodaOrderRepository) {
        this.customerRepository = customerRepository;
        this.sodaOrderService = sodaOrderService;
        this.sodaOrderRepository = sodaOrderRepository;

        sodaUpcs.add(SodaOrderBootStrap.SODA_1_UPC);
        sodaUpcs.add(SodaOrderBootStrap.SODA_2_UPC);
        sodaUpcs.add(SodaOrderBootStrap.SODA_3_UPC);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeTastingRoomOrder(){

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(SodaOrderBootStrap.TASTING_ROOM);

        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");

            customerList.forEach(customer -> log.debug(customer.toString()));
        }
    }

    private void doPlaceOrder(Customer customer) {
        String sodaToOrder = getRandomSodaUpc();

        SodaOrderLineDto sodaOrderLine = SodaOrderLineDto.builder()
                .upc(sodaToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<SodaOrderLineDto> sodaOrderLineSet = new ArrayList<>();
        sodaOrderLineSet.add(sodaOrderLine);

        SodaOrderDto sodaOrder = SodaOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .sodaOrderLines(sodaOrderLineSet)
                .build();

        SodaOrderDto savedOrder = sodaOrderService.placeOrder(customer.getId(), sodaOrder);

    }

    private String getRandomSodaUpc() {
        return sodaUpcs.get(new Random().nextInt(sodaUpcs.size() -0));
    }
}
