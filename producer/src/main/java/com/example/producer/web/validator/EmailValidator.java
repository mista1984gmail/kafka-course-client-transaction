package com.example.producer.web.validator;


import com.example.producer.domain.Client;
import com.example.producer.service.ClientService;
import com.example.producer.service.resttemplate.ClientRestTemplateResponse;
import com.example.producer.service.resttemplate.service.ProducerApiClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class EmailValidator implements Validator {
    private final ClientService clientService;
    private final ModelMapper modelMapper;
    private final ProducerApiClient producerApiClient;

    @Autowired
    public EmailValidator(ClientService clientService, ModelMapper modelMapper, ProducerApiClient producerApiClient) {
        this.clientService = clientService;
        this.modelMapper = modelMapper;
        this.producerApiClient = producerApiClient;
    }
    @Override
    public boolean supports(Class<?> aClass) {
        return Client.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Client person = modelMapper.map(o, Client.class);
        ClientRestTemplateResponse clientFromDb = producerApiClient.getClientByEmail(person.getEmail());
        if (clientFromDb.getEmail() != null) {
            if (clientFromDb.getEmail().equals(person.getEmail()))
                errors.rejectValue("email", "", "A person with this email already exists");
        }
    }
}
