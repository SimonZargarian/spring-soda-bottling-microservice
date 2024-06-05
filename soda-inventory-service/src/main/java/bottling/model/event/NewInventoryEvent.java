package bottling.model.event;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends SodaEvent {
    public NewInventoryEvent(SodaDto sodaDto) {
        super(sodaDto);
    }
}
