package bottling.model.events;

import bottling.model.SodaDto;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BottlingSodaEvent extends SodaEvent{

    public BottlingSodaEvent(SodaDto sodaDto) {
        super(sodaDto);
    }
}
