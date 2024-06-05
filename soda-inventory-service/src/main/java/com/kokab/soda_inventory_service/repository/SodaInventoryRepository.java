package com.kokab.soda_inventory_service.repository;

import com.kokab.soda_inventory_service.domain.SodaInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface SodaInventoryRepository extends JpaRepository<SodaInventory, UUID> {

    List<SodaInventory> findAllBySodaId(UUID sodaId);

    List<SodaInventory> findAllByUpc(String upc);
}
