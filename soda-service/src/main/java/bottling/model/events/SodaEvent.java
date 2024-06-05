package bottling.model.events;

import bottling.model.SodaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SodaEvent implements Serializable {

    static final long serialVersionUID = -5781515597148163111L;

    private SodaDto beerDto;
}