package it.pagopa.wf.engine.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HttpRequestUtils {

    public static HttpEntity<String> buildHttpEntity(String body, HttpHeaders headers) {
        return new HttpEntity<>(body, headers);
    }

    public static HttpHeaders createHttpHeaders(Map<String, Object> headersMap) {
        if (CollectionUtils.isEmpty(headersMap)) {
            headersMap = new HashMap<>();
        }
        return HttpRequestUtils.fromMapToHeaders(headersMap);
    }

    public static URI buildURI(String basePath, String endpoint, Map<String, String> pathParams) {
        String url = basePath.concat(endpoint);
        return buildURI(url, pathParams);
    }

    public static URI buildURI(String endpoint, Map<String, String> pathParams) {
        Assert.notNull(pathParams, "pathParams can be empty but cannot be null");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(endpoint);
        return builder.buildAndExpand(pathParams).toUri();
    }

    public static HttpHeaders fromMapToHeaders(Map<String, Object> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                String stringValue = (String) value;
                if (StringUtils.isBlank(stringValue)) {
                    throw new RuntimeException("declared path param cannot be empty or null");
                }
                multiValueMap.put(key, Collections.singletonList(stringValue));
            } else {
                throw new RuntimeException("Value for key " + key + " is not a String");
            }
        }

        return new HttpHeaders(multiValueMap);
    }






    public static HttpMethod httpMethodFromValue(String httpMethodString) {
        return switch (httpMethodString.toUpperCase()) {
            case "GET" -> HttpMethod.GET;
            case "HEAD" -> HttpMethod.HEAD;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "PATCH" -> HttpMethod.PATCH;
            case "DELETE" -> HttpMethod.DELETE;
            case "OPTIONS" -> HttpMethod.OPTIONS;
            case "TRACE" -> HttpMethod.TRACE;
            default -> throw new RuntimeException(String.format("Http method %s does not exist", httpMethodString));
        };
    }
}
