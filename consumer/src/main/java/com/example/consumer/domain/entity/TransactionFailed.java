package com.example.consumer.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_failed")
public class TransactionFailed {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="bank",
            nullable = false)
    private String bank;

    @Column(name="order_type",
            nullable = false)
    @Enumerated(value = EnumType.STRING)
    private OrderType orderType;

    @Column(name="quantity",
            nullable = false)
    private Integer quantity;

    @Column(name="price",
            nullable = false)
    private Double price;

    @Column(name="created_at",
            nullable = false)
    private LocalDateTime createdAt;

    @Column(name="total_cost")
    private BigDecimal totalCost;

    @Column(name="incorrect_id")
    private Long incorrectId;

    @Column(name="error",
            nullable = false)
    private String error;


}
