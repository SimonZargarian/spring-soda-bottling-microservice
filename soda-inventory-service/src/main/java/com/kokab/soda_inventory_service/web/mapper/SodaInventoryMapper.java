package com.kokab.soda_inventory_service.web.mapper;

import bottling.model.SodaInventoryDto;
import com.kokab.soda_inventory_service.domain.SodaInventory;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface SodaInventoryMapper {

    SodaInventory sodaInventoryDtoToSodaInventory(SodaInventoryDto sodaInventoryDTO);

    SodaInventoryDto sodaInventoryToSodaInventoryDto(SodaInventory sodaInventory);
}