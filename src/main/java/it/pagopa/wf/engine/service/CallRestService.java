package it.pagopa.wf.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.client.variable.ClientValues;
import org.camunda.bpm.client.variable.value.JsonValue;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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


    public VariableMap callAdapter(Map<String,Object> variables) throws JsonProcessingException {

        String jsonBody = objectMapper.writeValueAsString(variables);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
//        Map<String, Object> variablesResponse = null;
//
//        try {
//            variablesResponse = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
//            // Stampa o usa la mappa come desideri
//            System.out.println("Mappa deserializzata: " + variables);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        SpinJsonNode headersJsonNode = JSON(response.getBody());

        JsonValue jsonValue;
        if (StringUtils.isNotBlank(response.getBody())
                && response.getStatusCode() != null
                && response.getStatusCode().is2xxSuccessful()) {
            jsonValue = ClientValues.jsonValue(response.getBody());
        } else {
            jsonValue = ClientValues.jsonValue("{}");
        }
        VariableMap output = Variables.createVariables();
        output.putValue("response", headersJsonNode);
        output.putValue("statusCode", response.getStatusCode().value());

        return output;
    }


}
