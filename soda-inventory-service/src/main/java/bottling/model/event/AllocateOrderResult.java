package bottling.model.event;

import bottling.model.SodaOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllocateOrderResult {
    private SodaOrderDto sodaOrderDto;
    private Boolean allocationError = false;
    private Boolean pendingInventory = false;
}