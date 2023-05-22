package com.example.consumer.service;

import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.service.messaging.event.TransactionEvent;


public interface TransactionFailedService {
    TransactionFailed save(TransactionFailed transactionFailed);
    TransactionFailed getTransactionFailed(TransactionEvent transactionEvent);
}
