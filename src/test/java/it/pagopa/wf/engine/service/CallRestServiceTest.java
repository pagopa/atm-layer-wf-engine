package it.pagopa.wf.engine.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CallRestServiceTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    CallRestService callRestService;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        MockitoAnnotations.openMocks(this);

        Field apiUrlField = CallRestService.class.getDeclaredField("apiUrl");
        apiUrlField.setAccessible(true);
        apiUrlField.set(callRestService, "http://mock.api/endpoint");
    }

    @Test
    void testCallAdapter() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer token");
        ResponseEntity<String> mockResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        String result = callRestService.callAdapter(headers);

        assertEquals("Success", result);

        ArgumentCaptor<HttpEntity<String>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq("http://mock.api/endpoint"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(String.class));

        HttpEntity<String> capturedHttpEntity = httpEntityCaptor.getValue();
        assertEquals(headers, capturedHttpEntity.getHeaders());
    }
}
