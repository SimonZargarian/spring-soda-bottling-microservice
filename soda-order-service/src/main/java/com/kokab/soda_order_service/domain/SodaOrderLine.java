package com.kokab.soda_order_service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SodaOrderLine extends BaseEntity{

    @Builder
    public SodaOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         SodaOrder sodaOrder, UUID sodaId, String upc, Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.sodaOrder = sodaOrder;
        this.sodaId = sodaId;
        this.upc = upc;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private SodaOrder sodaOrder;

    private UUID sodaId;
    private String upc;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}

