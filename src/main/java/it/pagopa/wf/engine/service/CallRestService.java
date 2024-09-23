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

    private static final String RESPONSE_STRING = "response";

    private static final String STATUS_CODE_STRING = "statusCode";

    private static final String ERROR_STRING = "error";


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

                if (variables.containsKey("flow")
                        && variables.get("flow") instanceof String
                            && "AUTH".equals(variables.get("flow"))){
                    log.info("--TEMPORARY-- Setting milAccessToken variable in output. Received responsejsonnode: {}", responseJsonNode);
                    output.putValue("millAccessToken", responseJsonNode.prop("access_token").stringValue());
                } else {
                    output.putValue(RESPONSE_STRING, responseJsonNode);
                }
                output.putValue("responseHeaders", responseHeaders);
                output.putValue(STATUS_CODE_STRING, response.getStatusCode().value());

            } catch (HttpStatusCodeException e) {
                // Gestione delle risposte HTTP non 2xx
                output.putValue(ERROR_STRING, "HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
                output.putValue(STATUS_CODE_STRING, e.getStatusCode().value());
                output.putValue(RESPONSE_STRING, JSON("{}"));
                log.error("HTTP error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());

            } catch (ResourceAccessException e) {
                // Gestione degli errori di accesso alle risorse, come timeout o problemi di connettività
                output.putValue(ERROR_STRING, "Resource access error: " + e.getMessage());
                output.putValue(STATUS_CODE_STRING, HttpStatus.GATEWAY_TIMEOUT.value());
                output.putValue(RESPONSE_STRING, JSON("{}"));
                log.error("Resource access error: " +  e.getMessage());
            } catch (Exception e) {
                // Gestione di altre eccezioni generiche
                output.putValue(ERROR_STRING, "An unexpected error occurred: " + e.getMessage());
                output.putValue(STATUS_CODE_STRING, HttpStatus.INTERNAL_SERVER_ERROR.value());
                output.putValue(RESPONSE_STRING, JSON("{}"));
                log.error("An unexpected error occurred: " +  e.getMessage());
            }

            log.info("Status code with transactionId: {} and url: {} is: {}",variables.get("transactionId"),variables.get("url"), output.get(STATUS_CODE_STRING));
            output.putValue("externalComm", true);
            return output;
        }

}
