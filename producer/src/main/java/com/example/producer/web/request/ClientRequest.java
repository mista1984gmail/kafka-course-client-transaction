package com.example.producer.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String email;
}
