package com.example.util;


import com.example.consumer.domain.entity.Client;
import com.example.consumer.service.dto.ClientDto;
import com.example.consumer.service.messaging.event.ClientEvent;

public class FakeClient {

    public static ClientEvent getClientEvent(){
        return new ClientEvent(
                "Ivan",
                "Ivanov",
                "Grodno, Pobedy, 123/45",
                "ivan2000@gmail.com",
                "+375291234567");
    }

    public static Client getClient(){
        return new Client(
                "Ivan",
                "Ivanov",
                "Grodno, Pobedy, 123/45",
                "ivan2000@gmail.com",
                "+375291234567");
    }
    public static ClientDto getClientDto(){
        return new ClientDto(
                "Ivan",
                "Ivanov",
                "Grodno, Pobedy, 123/45",
                "ivan2000@gmail.com",
                "+375291234567");
    }

}
