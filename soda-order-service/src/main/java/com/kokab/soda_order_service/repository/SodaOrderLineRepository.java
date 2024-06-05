package com.kokab.soda_order_service.repository;

import com.kokab.soda_order_service.domain.SodaOrderLine;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface SodaOrderLineRepository extends PagingAndSortingRepository<SodaOrderLine, UUID>{
}
