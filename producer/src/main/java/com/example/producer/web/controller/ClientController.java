package com.example.producer.web.controller;

import com.example.producer.service.ClientService;
import com.example.producer.service.dto.ClientDto;
import com.example.producer.web.request.ClientRequest;
import com.example.producer.web.response.ClientResponse;
import lombok.AllArgsConstructor;
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
@RequestMapping("/api/v1/client")
public class ClientController {

    private final ClientService clientService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse save(@RequestBody ClientRequest clientRequest) {
        log.info("Send client from controller {}", clientRequest);
        return modelMapper.map(clientService.sendClient(
                modelMapper.map(clientRequest, ClientDto.class)), ClientResponse.class);
    }

}
