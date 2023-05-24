package com.example.util;

import com.example.consumer.domain.entity.OrderType;
import com.example.consumer.domain.entity.Transaction;
import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.service.dto.TransactionDto;
import com.example.consumer.service.messaging.event.TransactionEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FakeTransaction {

    public static TransactionEvent getTransactionEvent(){
        return new TransactionEvent(
                "PriorBank",
                1L,
                OrderType.OUTCOME,
                5,
                2.22,
                LocalDateTime.of(2023,05,19,15,56,00));
    }
    public static Transaction getTransaction(){
        return new Transaction(
                1L,
                "PriorBank",
                OrderType.OUTCOME,
                5,
                2.22,
                LocalDateTime.of(2023,05,19,15,56,00),
                new BigDecimal(11.1),
                FakeClient.getClient());

    }
    public static TransactionDto getTransactionDto(){
        return new TransactionDto(
                "PriorBank",
                1L,
                OrderType.OUTCOME,
                5,
                2.22,
                LocalDateTime.of(2023,05,19,15,56,00));
    }
    public static TransactionFailed getTransactionFailed(){
        return new TransactionFailed(
                1L,
                "PriorBank",
                OrderType.OUTCOME,
                5,
                2.22,
                LocalDateTime.of(2023,05,19,15,56,00),
                new BigDecimal(11.1),
                5L,
                "Transaction canceled, can not find client id."
                );
    }
}
