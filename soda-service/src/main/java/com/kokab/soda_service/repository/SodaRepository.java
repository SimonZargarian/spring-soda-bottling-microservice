package com.kokab.soda_service.repository;

import bottling.model.SodaStyleEnum;
import com.kokab.soda_service.domain.Soda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface SodaRepository extends JpaRepository<Soda, UUID>{

    Page<Soda> findAllBySodaName(String sodaName, Pageable pageable);

    Page<Soda> findAllBySodaStyle(SodaStyleEnum sodaStyle, Pageable pageable);

    Page<Soda> findAllBySodaNameAndSodaStyle(String sodaName, SodaStyleEnum sodasStyle, Pageable pageable);

    Soda findByUpc(String upc);
}
