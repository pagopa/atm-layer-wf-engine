package it.pagopa.wf.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");

        when(objectMapper.writeValueAsString(variables)).thenReturn(jsonBody);

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        String result = callRestService.callAdapter(variables);

        assertEquals("Success", result);

        ArgumentCaptor<HttpEntity<String>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(String.class));

        HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();
        assertEquals(jsonBody, capturedHttpEntity.getBody());
        assertEquals(expectedHeaders, capturedHttpEntity.getHeaders());

        verify(objectMapper).writeValueAsString(variables);
    }
}
