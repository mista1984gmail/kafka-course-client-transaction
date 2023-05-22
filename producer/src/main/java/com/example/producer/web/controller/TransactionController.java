package com.example.producer.web.controller;

import com.example.producer.service.TransactionService;
import com.example.producer.service.dto.TransactionDto;
import com.example.producer.web.request.TransactionRequest;
import com.example.producer.web.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse save(@RequestBody TransactionRequest transactionRequest) {
        log.info("Send transaction from controller {}", transactionRequest);
        return modelMapper.map(transactionService.sendTransaction(
                modelMapper.map(transactionRequest, TransactionDto.class)), TransactionResponse.class);
    }

}
