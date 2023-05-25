package com.example.producer.web.request;

import com.example.producer.domain.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull
    private String bank;
    @NotNull
    private Long clientId;
    @NotNull
    private OrderType orderType;
    @NotNull
    private Integer quantity;
    @NotNull
    private Double price;
    @NotNull
    private LocalDateTime createdAt;
}
