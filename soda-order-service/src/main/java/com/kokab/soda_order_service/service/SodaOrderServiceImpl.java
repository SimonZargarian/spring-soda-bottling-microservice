package com.kokab.soda_order_service.service;

import bottling.model.SodaOrderDto;
import bottling.model.SodaOrderPagedList;
import com.kokab.soda_order_service.domain.Customer;
import com.kokab.soda_order_service.domain.SodaOrder;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import com.kokab.soda_order_service.repository.CustomerRepository;
import com.kokab.soda_order_service.repository.SodaOrderRepository;
import com.kokab.soda_order_service.web.mapper.SodaOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SodaOrderServiceImpl  implements SodaOrderService {

    private final SodaOrderRepository sodaOrderRepository;
    private final CustomerRepository customerRepository;
    private final SodaOrderMapper sodaOrderMapper;
    private final SodaOrderManager sodaOrderManager;

    @Override
    public SodaOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<SodaOrder> sodaOrderPage =
                    sodaOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new SodaOrderPagedList(sodaOrderPage
                    .stream()
                    .map(sodaOrderMapper::sodaOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    sodaOrderPage.getPageable().getPageNumber(),
                    sodaOrderPage.getPageable().getPageSize()),
                    sodaOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public SodaOrderDto placeOrder(UUID customerId, SodaOrderDto sodaOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            SodaOrder sodaOrder = sodaOrderMapper.dtoToSodaOrder(sodaOrderDto);
            sodaOrder.setId(null); //should not be set by outside client
            sodaOrder.setCustomer(customerOptional.get());
            sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);

            sodaOrder.getSodaOrderLines().forEach(line -> line.setSodaOrder(sodaOrder));

            SodaOrder savedSodaOrder = sodaOrderManager.newSodaOrder(sodaOrder);

            log.debug("Saved Soda Order: " + sodaOrder.getId());

            return sodaOrderMapper.sodaOrderToDto(savedSodaOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public SodaOrderDto getOrderById(UUID customerId, UUID orderId) {
        return sodaOrderMapper.sodaOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        sodaOrderManager.sodaOrderPickedUp(orderId);
    }

    private SodaOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(orderId);

            if(sodaOrderOptional.isPresent()){
                SodaOrder sodaOrder = sodaOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if(sodaOrder.getCustomer().getId().equals(customerId)){
                    return sodaOrder;
                }
            }
            throw new RuntimeException("Soda Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
