package com.kokab.soda_service.service.inventory;

import java.util.UUID;

public interface SodaInventoryService {
    Integer getOnHandInventory(UUID sodaId);
}
