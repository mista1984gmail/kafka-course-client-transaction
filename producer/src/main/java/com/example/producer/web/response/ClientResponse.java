package com.example.producer.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String telephone;
    private String clientCode;
}
