package com.example.producer.service.impl;

import com.example.producer.service.TransactionService;
import com.example.producer.service.dto.TransactionDto;
import com.example.producer.service.messaging.producer.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final Producer producer;

    @Override
    public TransactionDto sendTransaction(TransactionDto transactionDto) {
        log.info("Send transaction from service {}", transactionDto);
        return producer.sendTransaction(transactionDto);
    }
}
