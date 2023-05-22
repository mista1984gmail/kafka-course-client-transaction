package com.example.producer.service;


import com.example.producer.service.dto.TransactionDto;

public interface TransactionService {
    TransactionDto sendTransaction(TransactionDto transactionDto);
}
