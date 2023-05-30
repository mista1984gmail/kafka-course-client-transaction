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
@Table(name = "transaction")
public class Transaction {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="bank",
            nullable = false)
    private String bank;

    @Column(name="order_type",
            nullable = false)
    @Enumerated(EnumType.STRING)
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

    @Column(name="client_unique_code",
            nullable = false)
    private String clientUniqueCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name="client_id")
    @Fetch(FetchMode.JOIN)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client owner;

}
