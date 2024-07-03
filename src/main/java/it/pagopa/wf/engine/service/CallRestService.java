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


        SpinJsonNode responseJsonNode = JSON(response.getBody());
        SpinJsonNode responseHeaders = JSON(response.getHeaders());

        JsonValue jsonValue;
        if (StringUtils.isNotBlank(response.getBody())
                && response.getStatusCode() != null
                && response.getStatusCode().is2xxSuccessful()) {
            jsonValue = ClientValues.jsonValue(response.getBody());
        } else {
            jsonValue = ClientValues.jsonValue("{}");
        }
        VariableMap output = Variables.createVariables();
        output.putValue("response", responseJsonNode);
        output.putValue("statusCode", response.getStatusCode().value());
        output.putValue("responseHeaders", responseHeaders);

        return output;
    }


}
