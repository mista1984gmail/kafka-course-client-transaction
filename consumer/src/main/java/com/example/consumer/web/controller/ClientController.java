package com.example.consumer.web.controller;

import com.example.consumer.domain.entity.Client;
import com.example.consumer.service.ClientService;
import com.example.consumer.service.resttamplate.ClientRestTemplateResponse;
import com.example.consumer.web.response.ClientResponse;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client")
public class ClientController {

    private final ClientService clientService;

    private final ModelMapper modelMapper;
    @Operation(summary = "Find all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find clients", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientResponse> findAll() {
        log.info("Find all clients");
        return clientService.findAll().stream()
                .map(client -> modelMapper.map(client, ClientResponse.class))
                .collect(Collectors.toList());
    }
    @Operation(summary = "Get client by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get client by email", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public ClientRestTemplateResponse findByEmail(@RequestParam String email) {
        log.info("Find client with email: {}", email);
        return modelMapper.map(clientService.findByEmail(email).orElse(new Client()), ClientRestTemplateResponse.class);
    }


}
