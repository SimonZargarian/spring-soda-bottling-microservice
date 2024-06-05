package com.kokab.soda_inventory_service.service;

import bottling.model.SodaOrderDto;
import bottling.model.SodaOrderLineDto;
import com.kokab.soda_inventory_service.domain.SodaInventory;
import com.kokab.soda_inventory_service.repository.SodaInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final SodaInventoryRepository sodaInventoryRepository;

    @Override
    public Boolean allocateOrder(SodaOrderDto sodaOrderDto) {
        log.debug("Allocating OrderId: " + sodaOrderDto.getId());

        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        sodaOrderDto.getSodaOrderLines().forEach(sodaOrderLine -> {
            if ((((sodaOrderLine.getOrderQuantity() != null ? sodaOrderLine.getOrderQuantity() : 0)
                    - (sodaOrderLine.getQuantityAllocated() != null ? sodaOrderLine.getQuantityAllocated() : 0)) > 0)) {
                allocateSodaOrderLine(sodaOrderLine);
            }
            totalOrdered.set(totalOrdered.get() + sodaOrderLine.getOrderQuantity());
            totalAllocated.set(totalAllocated.get() + (sodaOrderLine.getQuantityAllocated() != null ? sodaOrderLine.getQuantityAllocated() : 0));
        });

        log.debug("Total Ordered: " + totalOrdered.get() + " Total Allocated: " + totalAllocated.get());

        return totalOrdered.get() == totalAllocated.get();
    }

    private void allocateSodaOrderLine(SodaOrderLineDto sodaOrderLine) {
        List<SodaInventory> sodaInventoryList = sodaInventoryRepository.findAllByUpc(sodaOrderLine.getUpc());

        sodaInventoryList.forEach(sodaInventory -> {
            int inventory = (sodaInventory.getQuantityOnHand() == null) ? 0 : sodaInventory.getQuantityOnHand();
            int orderQty = (sodaOrderLine.getOrderQuantity() == null) ? 0 : sodaOrderLine.getOrderQuantity();
            int allocatedQty = (sodaOrderLine.getQuantityAllocated() == null) ? 0 : sodaOrderLine.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory >= qtyToAllocate) { // full allocation
                inventory = inventory - qtyToAllocate;
                sodaOrderLine.setQuantityAllocated(orderQty);
                sodaInventory.setQuantityOnHand(inventory);

                sodaInventoryRepository.save(sodaInventory);
            } else if (inventory > 0) { //partial allocation
                sodaOrderLine.setQuantityAllocated(allocatedQty + inventory);
                sodaInventory.setQuantityOnHand(0);

            }

            if (sodaInventory.getQuantityOnHand() == 0) {
                sodaInventoryRepository.delete(sodaInventory);
            }
        });

    }

    @Override
    public void deallocateOrder(SodaOrderDto sodaOrderDto) {
        sodaOrderDto.getSodaOrderLines().forEach(sodaOrderLineDto -> {
            SodaInventory sodaInventory = SodaInventory.builder()
                    .sodaId(sodaOrderLineDto.getSodaId())
                    .upc(sodaOrderLineDto.getUpc())
                    .quantityOnHand(sodaOrderLineDto.getQuantityAllocated())
                    .build();

            SodaInventory savedInventory = sodaInventoryRepository.save(sodaInventory);

            log.debug("Saved Inventory for soda upc: " + savedInventory.getUpc() + " inventory id: " + savedInventory.getId());
        });
    }
}