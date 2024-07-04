package it.pagopa.wf.engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CallRestService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Value("${external.api.adapter.url}")
    private String apiUrl;





        public VariableMap callAdapter(Map<String,Object> variables)  {
            RestTemplate restTemplate = new RestTemplate();
            VariableMap output = Variables.createVariables();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                String jsonBody = objectMapper.writeValueAsString(variables);
                HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

                SpinJsonNode responseJsonNode = JSON(response.getBody());
                SpinJsonNode responseHeaders = JSON(response.getHeaders());

                output.putValue("response", responseJsonNode);
                output.putValue("responseHeaders", responseHeaders);
                output.putValue("statusCode", response.getStatusCode().value());

            } catch (HttpStatusCodeException e) {
                // Gestione delle risposte HTTP non 2xx
                output.putValue("error", "HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
                output.putValue("statusCode", e.getStatusCode().value());

            } catch (ResourceAccessException e) {
                // Gestione degli errori di accesso alle risorse, come timeout o problemi di connettività
                output.putValue("error", "Resource access error: " + e.getMessage());
                output.putValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR);

            } catch (Exception e) {
                // Gestione di altre eccezioni generiche
                output.putValue("error", "An unexpected error occurred: " + e.getMessage());
                output.putValue("statusCode", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return output;
        }



}
