package com.example.producer.service.resttemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRestTemplateResponse {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String telephone;
    private String clientCode;
}
