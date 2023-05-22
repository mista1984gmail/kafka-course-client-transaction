package com.example.producer.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
}
