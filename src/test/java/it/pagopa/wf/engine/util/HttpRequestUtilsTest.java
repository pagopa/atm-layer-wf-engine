package it.pagopa.wf.engine.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestUtilsTest {

    @Test
    void testBuildHttpEntity() {
        String body = "Test Body";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer token");

        HttpEntity<String> httpEntity = HttpRequestUtils.buildHttpEntity(body, headers);

        assertEquals(body, httpEntity.getBody());
        assertEquals(headers, httpEntity.getHeaders());
    }

    @Test
    void testCreateHttpHeaders() {
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Authorization", "Bearer token");

        HttpHeaders headers = HttpRequestUtils.createHttpHeaders(headersMap);

        assertEquals("Bearer token", headers.getFirst("Authorization"));
    }

    @Test
    void testCreateHttpHeaders_EmptyMap() {
        HttpHeaders headers = HttpRequestUtils.createHttpHeaders(Collections.emptyMap());

        assertTrue(headers.isEmpty());
    }

    @Test
    void testBuildURI_WithBasePathAndEndpoint() {
        String basePath = "http://example.com";
        String endpoint = "/api/test";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "123");

        URI uri = HttpRequestUtils.buildURI(basePath, endpoint, pathParams);

        assertEquals("http://example.com/api/test", uri.toString());
    }

    @Test
    void testBuildURI_WithPathParams() {
        String endpoint = "http://example.com/api/{id}";
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "123");

        URI uri = HttpRequestUtils.buildURI(endpoint, pathParams);

        assertEquals("http://example.com/api/123", uri.toString());
    }

    @Test
    void testFromMapToHeaders() {
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Authorization", "Bearer token");

        HttpHeaders headers = HttpRequestUtils.fromMapToHeaders(headersMap);

        assertEquals("Bearer token", headers.getFirst("Authorization"));
    }

    @Test
    void testFromMapToHeaders_EmptyValue() {
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Authorization", "");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> HttpRequestUtils.fromMapToHeaders(headersMap));
        assertEquals("declared path param cannot be empty or null", exception.getMessage());
    }

    @Test
    void testFromMapToHeaders_NonStringValue() {
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Authorization", 123);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> HttpRequestUtils.fromMapToHeaders(headersMap));
        assertEquals("Value for key Authorization is not a String", exception.getMessage());
    }

    @Test
    void testHttpMethodFromValue() {
        assertEquals(HttpMethod.GET, HttpRequestUtils.httpMethodFromValue("GET"));
        assertEquals(HttpMethod.HEAD, HttpRequestUtils.httpMethodFromValue("HEAD"));
        assertEquals(HttpMethod.POST, HttpRequestUtils.httpMethodFromValue("POST"));
        assertEquals(HttpMethod.PUT, HttpRequestUtils.httpMethodFromValue("PUT"));
        assertEquals(HttpMethod.PATCH, HttpRequestUtils.httpMethodFromValue("PATCH"));
        assertEquals(HttpMethod.DELETE, HttpRequestUtils.httpMethodFromValue("DELETE"));
        assertEquals(HttpMethod.OPTIONS, HttpRequestUtils.httpMethodFromValue("OPTIONS"));
        assertEquals(HttpMethod.TRACE, HttpRequestUtils.httpMethodFromValue("TRACE"));
    }

    @Test
    void testHttpMethodFromValue_InvalidMethod() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> HttpRequestUtils.httpMethodFromValue("INVALID"));
        assertEquals("Http method INVALID does not exist", exception.getMessage());
    }
}
