package com.example.consumer.service;

import com.example.consumer.domain.entity.TransactionFailed;
import com.example.consumer.service.messaging.event.TransactionEvent;

import java.util.List;


public interface TransactionFailedService {
    TransactionFailed save(TransactionFailed transactionFailed);
    TransactionFailed getTransactionFailed(TransactionEvent transactionEvent);
    List<TransactionFailed> findAll();
    List<TransactionFailed> findByIncorrectId(Long id);
    void delete(Long id);
}
