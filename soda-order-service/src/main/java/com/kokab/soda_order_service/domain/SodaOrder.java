package com.kokab.soda_order_service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SodaOrder extends BaseEntity{

    @Builder
    public SodaOrder(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate, String customerRef, Customer customer,
                     Set<SodaOrderLine> sodaOrderLines, SodaOrderStatusEnum orderStatus,
                     String orderStatusCallbackUrl) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerRef = customerRef;
        this.customer = customer;
        this.sodaOrderLines = sodaOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "sodaOrder", cascade = CascadeType.ALL)
    @Fetch(FetchMode.JOIN)
    private Set<SodaOrderLine> sodaOrderLines;

    private SodaOrderStatusEnum orderStatus = SodaOrderStatusEnum.NEW;
    private String orderStatusCallbackUrl;
}

