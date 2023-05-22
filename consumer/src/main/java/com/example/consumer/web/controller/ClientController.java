package com.example.consumer.web.controller;

import com.example.consumer.service.ClientService;
import com.example.consumer.web.response.ClientResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClientResponse> findAll() {
        log.info("Find all clients");
        return clientService.findAll().stream()
                .map(client -> modelMapper.map(client, ClientResponse.class))
                .collect(Collectors.toList());
    }


}
