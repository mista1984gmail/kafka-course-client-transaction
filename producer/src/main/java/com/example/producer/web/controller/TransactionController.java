package com.example.producer.web.controller;

import com.example.producer.service.TransactionService;
import com.example.producer.service.dto.TransactionDto;
import com.example.producer.web.request.TransactionRequest;
import com.example.producer.web.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Save transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save transaction", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse save(@RequestBody TransactionRequest transactionRequest) {
        log.info("Send transaction from controller {}", transactionRequest);
        return modelMapper.map(transactionService.sendTransaction(
                modelMapper.map(transactionRequest, TransactionDto.class)), TransactionResponse.class);
    }

}
