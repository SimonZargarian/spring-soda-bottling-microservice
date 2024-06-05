package com.kokab.soda_order_service.repository;

import com.kokab.soda_order_service.domain.Customer;
import com.kokab.soda_order_service.domain.SodaOrder;
import com.kokab.soda_order_service.domain.SodaOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface SodaOrderRepository extends JpaRepository<SodaOrder, UUID> {

    Page<SodaOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<SodaOrder> findAllByOrderStatus(SodaOrderStatusEnum orderStatusEnum);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // SodaOrder findOneById(UUID id);
}
