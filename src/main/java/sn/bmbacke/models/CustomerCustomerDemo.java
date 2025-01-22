package sn.bmbacke.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CustomerCustomerDemo")
public class CustomerCustomerDemo {
    @EmbeddedId
    private CustomerCustomerDemoId id;

    @ManyToOne
    @MapsId("customerId")
    @JoinColumn(name = "CustomerID")
    private Customer customer;

    @ManyToOne
    @MapsId("customerTypeId")
    @JoinColumn(name = "CustomerTypeID")
    private CustomerDemographics customerDemographics;
}
