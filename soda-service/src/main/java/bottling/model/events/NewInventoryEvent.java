package bottling.model.events;

import bottling.model.SodaDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends SodaEvent {
    public NewInventoryEvent(SodaDto sodaDto) {
        super(sodaDto);
    }
}
