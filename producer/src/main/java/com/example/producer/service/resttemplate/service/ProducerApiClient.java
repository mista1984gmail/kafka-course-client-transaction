package com.example.producer.service.resttemplate.service;

import com.example.producer.service.resttemplate.ClientRestTemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@ConfigurationProperties(value = "resttemplate.consumer.client", ignoreUnknownFields = false)
public class ProducerApiClient {

  @Autowired
  private RestTemplate restTemplate;

  public final String CLIENT_UNIT_PATH = "/api/v1/client/";

  private String apiHost;

  public ClientRestTemplateResponse getClientByEmail(String email) {
    log.info("Get client from consumer by email: {}", email);
    return restTemplate.getForObject(apiHost + CLIENT_UNIT_PATH + "email/?email=" + email,
        ClientRestTemplateResponse.class);
  }

  public ClientRestTemplateResponse getClientByCode(String code) {
    log.info("Get client from consumer by code: {}", code);
    return restTemplate.getForObject(apiHost + CLIENT_UNIT_PATH + code,
            ClientRestTemplateResponse.class);
  }

  public void setApiHost(String apiHost) {
    this.apiHost = apiHost;
  }
}
