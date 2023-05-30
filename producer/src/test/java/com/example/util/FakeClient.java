package com.example.util;

import com.example.producer.service.messaging.event.ClientSendEvent;


public class FakeClient {

    public static ClientSendEvent getClientSendEvent(){
        return new ClientSendEvent(
                "Ivan",
                "Ivanov",
                "Grodno, Pobedy, 123/45",
                "ivan2000@gmail.com",
                "375291234567",
                "9be58def-859d-43b8-aacd-682c74419030"
                );
    }

}
