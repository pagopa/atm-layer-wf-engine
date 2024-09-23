package it.pagopa.wf.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.engine.variable.VariableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallRestServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CallRestService callRestService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        Field apiUrlField = CallRestService.class.getDeclaredField("apiUrl");
        apiUrlField.setAccessible(true);
        apiUrlField.set(callRestService, "http://mock.api/endpoint");
    }

    @Test
    void testCallAdapter() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.set("Content-Type", "application/json");

        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"outcome\":\"success\"}");

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        VariableMap result = callRestService.callAdapter(variables);

        assertEquals(200, result.get("statusCode"));

        ArgumentCaptor<HttpEntity<String>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(String.class));

        HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();
        assertEquals(jsonBody, capturedHttpEntity.getBody());
        assertEquals(expectedHeaders, capturedHttpEntity.getHeaders());

        verify(objectMapper).writeValueAsString(variables);
    }

    @Test
    void testCallAdapterHttpError() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        HttpStatusCodeException mockException = new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Not Found") {
            @Override
            public String getResponseBodyAsString() {
                return "{\"error\":\"Not Found\"}";
            }
        };

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(mockException);

        VariableMap result = callRestService.callAdapter(variables);

        assertEquals(404, result.get("statusCode"));
        assertEquals("HTTP error: 404 NOT_FOUND {\"error\":\"Not Found\"}", result.get("error"));
        verify(objectMapper).writeValueAsString(variables);
    }

    @Test
    void testCallAdapterResourceAccessError() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new ResourceAccessException("Timeout"));

        VariableMap result = callRestService.callAdapter(variables);

        assertEquals(504, result.get("statusCode"));  // 504 Gateway Timeout
        assertEquals("Resource access error: Timeout", result.get("error"));
        verify(objectMapper).writeValueAsString(variables);
    }

    @Test
    void testCallAdapterGenericError() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        String jsonBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        VariableMap result = callRestService.callAdapter(variables);

        assertEquals(500, result.get("statusCode"));  // 500 Internal Server Error
        assertEquals("An unexpected error occurred: Unexpected error", result.get("error"));
        verify(objectMapper).writeValueAsString(variables);
    }

    @Test
    void testCallAdapterAuthFlow() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("flow", "AUTH");
        variables.put("transactionId", "12345");

        String jsonBody = "{\"flow\":\"AUTH\",\"transactionId\":\"12345\"}";

        ResponseEntity<String> mockResponse = ResponseEntity.ok("{\"access_token\":\"abc123\"}");

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        VariableMap result = callRestService.callAdapter(variables);

        assertEquals("abc123", result.get("millAccessToken"));
        assertEquals(200, result.get("statusCode"));
        verify(objectMapper).writeValueAsString(variables);
    }

}
