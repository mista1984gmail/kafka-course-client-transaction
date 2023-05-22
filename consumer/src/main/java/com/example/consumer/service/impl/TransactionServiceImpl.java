package com.example.consumer.service.impl;

import com.example.consumer.domain.entity.Transaction;
import com.example.consumer.domain.repository.TransactionRepository;
import com.example.consumer.service.TransactionService;
import com.example.consumer.service.dto.TransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TransactionDto save(TransactionDto transactionDto) {
        log.debug("Save transaction: {}", transactionDto);
        return modelMapper.map(transactionRepository.save(
                modelMapper.map(transactionDto, Transaction.class)), TransactionDto.class);
    }
}
