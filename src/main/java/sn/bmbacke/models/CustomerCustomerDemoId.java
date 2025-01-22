package sn.bmbacke.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CustomerCustomerDemoId implements Serializable {
    @Column(name = "CustomerID", length = 5)
    private String customerId;

    @Column(name = "CustomerTypeID", length = 10)
    private String customerTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerCustomerDemoId that = (CustomerCustomerDemoId) o;
        return Objects.equals(customerId, that.customerId) &&
                Objects.equals(customerTypeId, that.customerTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerTypeId);
    }


}