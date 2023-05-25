package com.example.producer.service.resttemplate.service;

import com.example.producer.service.resttemplate.ClientRestTemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConfigurationProperties(value = "resttemplate.consumer.client", ignoreUnknownFields = false)
public class ProducerApiClient {

  @Autowired
  private RestTemplate restTemplate;

  public final String CLIENT_UNIT_PATH = "/api/v1/client/email/?email=";

  private String apiHost;

  public ClientRestTemplateResponse getClientByEmail(String email) {
    return restTemplate.getForObject(apiHost + CLIENT_UNIT_PATH + email,
        ClientRestTemplateResponse.class);
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }
}
