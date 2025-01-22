package sn.bmbacke.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;


@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Order Details")
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId id;

    @Column(name = "UnitPrice", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "Quantity", nullable = false)
    private Short quantity;

    @Column(name = "Discount", nullable = false)
    private Float discount;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "OrderID")
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "ProductID")
    private Product product;

}