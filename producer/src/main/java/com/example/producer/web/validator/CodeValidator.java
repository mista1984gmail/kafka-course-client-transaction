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
public class CodeValidator implements Validator {
    private final ClientService clientService;
    private final ModelMapper modelMapper;
    private final ProducerApiClient producerApiClient;

    @Autowired
    public CodeValidator(ClientService clientService, ModelMapper modelMapper, ProducerApiClient producerApiClient) {
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
        ClientRestTemplateResponse clientFromDb = producerApiClient.getClientByCode(person.getClientCode());
        if (clientFromDb.getClientCode() != null) {
            if (clientFromDb.getClientCode().equals(person.getClientCode()))
                errors.rejectValue("clientCode", "", "A person with this code already exists");
        }
    }
}
