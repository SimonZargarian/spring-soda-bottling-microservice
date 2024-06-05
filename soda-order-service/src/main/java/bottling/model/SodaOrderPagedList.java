package bottling.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
public class SodaOrderPagedList extends PageImpl<SodaOrderDto> {
    public SodaOrderPagedList(List<SodaOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public SodaOrderPagedList(List<SodaOrderDto> content) {
        super(content);
    }
}
