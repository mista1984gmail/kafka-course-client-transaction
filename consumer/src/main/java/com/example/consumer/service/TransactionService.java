package com.example.consumer.service;

import com.example.consumer.service.dto.TransactionDto;


public interface TransactionService {
    TransactionDto save(TransactionDto transactionDto);
}
