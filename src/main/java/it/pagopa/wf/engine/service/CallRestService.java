package it.pagopa.wf.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.camunda.spin.Spin.JSON;

@Service
@Slf4j
public class CallRestService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${external.api.adapter.url}")
    private String apiUrl;



        public VariableMap callAdapter(Map<String,Object> variables)  {
            VariableMap output = Variables.createVariables();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                String jsonBody = objectMapper.writeValueAsString(variables);
                log.info("Call url: {} with transactionId: {}", variables.get("url"),variables.get("transactionId"));
                HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

                SpinJsonNode responseJsonNode = JSON(StringUtils.isNotBlank(response.getBody()) ? response.getBody() : "{}");
                SpinJsonNode responseHeaders = JSON(response.getHeaders());

                if (variables.get("flow") != null && variables.get("flow").toString().equals("AUTH")){
                    log.info("--TEMPORARY-- Setting milAccessToken variable in output. Received responsejsonnode: {}", responseJsonNode);
                    output.putValue("millAccessToken", responseJsonNode.prop("access_token"));
                } else {
                    output.putValue("response", responseJsonNode);
                }
                output.putValue("responseHeaders", responseHeaders);
                output.putValue("statusCode", response.getStatusCode().value());

            } catch (HttpStatusCodeException e) {
                // Gestione delle risposte HTTP non 2xx
                output.putValue("error", "HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
                output.putValue("statusCode", e.getStatusCode().value());
                log.error("HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());

            } catch (ResourceAccessException e) {
                // Gestione degli errori di accesso alle risorse, come timeout o problemi di connettività
                output.putValue("error", "Resource access error: " + e.getMessage());
                output.putValue("statusCode", HttpStatus.GATEWAY_TIMEOUT.value());
                log.error("Resource access error: " +  e.getMessage());
            } catch (Exception e) {
                // Gestione di altre eccezioni generiche
                output.putValue("error", "An unexpected error occurred: " + e.getMessage());
                output.putValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
                log.error("An unexpected error occurred: " +  e.getMessage());
            }

            log.info("Status code with transactionId: {} and url: {} is: {}",variables.get("transactionId"),variables.get("url"), output.get("statusCode"));
            output.putValue("externalComm", true);
            return output;
        }


    public VariableMap callAuthAdapter(Map<String,Object> variables)  {
        VariableMap output = Variables.createVariables();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            String jsonBody = objectMapper.writeValueAsString(variables);
            log.info("Call url: {} with transactionId: {}", variables.get("url"),variables.get("transactionId"));
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            SpinJsonNode responseJsonNode = JSON(StringUtils.isNotBlank(response.getBody()) ? response.getBody() : "{}");
            SpinJsonNode responseHeaders = JSON(response.getHeaders());

            output.putValue("response", responseJsonNode);
            output.putValue("responseHeaders", responseHeaders);
            output.putValue("statusCode", response.getStatusCode().value());

        } catch (HttpStatusCodeException e) {
            // Gestione delle risposte HTTP non 2xx
            output.putValue("error", "HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            output.putValue("statusCode", e.getStatusCode().value());
            log.error("HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            // Gestione degli errori di accesso alle risorse, come timeout o problemi di connettività
            output.putValue("error", "Resource access error: " + e.getMessage());
            output.putValue("statusCode", HttpStatus.GATEWAY_TIMEOUT.value());
            log.error("Resource access error: " +  e.getMessage());
        } catch (Exception e) {
            // Gestione di altre eccezioni generiche
            output.putValue("error", "An unexpected error occurred: " + e.getMessage());
            output.putValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            log.error("An unexpected error occurred: " +  e.getMessage());
        }

        log.info("Status code with transactionId: {} and url: {} is: {}",variables.get("transactionId"),variables.get("url"), output.get("statusCode"));
        output.putValue("externalComm", true);
        return output;
    }



}
