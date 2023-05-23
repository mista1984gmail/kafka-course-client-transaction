package com.example.util;

import com.example.producer.domain.OrderType;
import com.example.producer.service.messaging.event.ClientSendEvent;
import com.example.producer.service.messaging.event.TransactionSendEvent;

import java.time.LocalDateTime;

public class FakeTransaction {

    public static TransactionSendEvent getTransactionSendEvent(){
        return new TransactionSendEvent(
                "PriorBank",
                1L,
                OrderType.OUTCOME,
                5,
                2.22,
                LocalDateTime.of(2023,05,19,15,56,00));
    }
}
