package com.example.consumer.service.messaging.event;

import com.example.consumer.domain.entity.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private String bank;
    private Long clientId;
    private OrderType orderType;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
}
