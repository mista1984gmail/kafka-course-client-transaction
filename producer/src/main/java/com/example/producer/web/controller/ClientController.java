package com.example.producer.web.controller;

import com.example.producer.exception.ClientWithEmailExistException;
import com.example.producer.service.ClientService;
import com.example.producer.service.dto.ClientDto;
import com.example.producer.web.request.ClientRequest;
import com.example.producer.web.response.ClientResponse;
import com.example.producer.web.validator.CodeValidator;
import com.example.producer.web.validator.EmailValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientController {

    private final ClientService clientService;
    private final ModelMapper modelMapper;
    private final EmailValidator emailValidator;
    private final CodeValidator codeValidator;

    @Operation(summary = "Save client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Save client", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse save( @Valid @RequestBody ClientRequest clientRequest,  BindingResult emailBinding, BindingResult codeBinding) {
        log.info("Validate client by unique email");
        emailValidator.validate(clientRequest, emailBinding);
        if (emailBinding.hasErrors()) {
            throw new ClientWithEmailExistException("Client with email " + clientRequest.getEmail() + " exist");
        }
        log.info("Validate client by unique code");
        codeValidator.validate(clientRequest, codeBinding);
        if (codeBinding.hasErrors()) {
            throw new ClientWithEmailExistException("Client with code " + clientRequest.getClientCode() + " exist");
        }
        log.info("Send client from producer controller {}", clientRequest);
        return modelMapper.map(clientService.sendClient(
                modelMapper.map(clientRequest, ClientDto.class)), ClientResponse.class);
    }

}
