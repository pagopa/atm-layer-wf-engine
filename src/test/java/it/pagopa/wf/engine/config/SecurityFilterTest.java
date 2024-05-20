package it.pagopa.wf.engine.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityFilterTest {
    SecurityFilter securityFilter = new SecurityFilter();
    @Test
    void testRegistrationBean(){
        HashMap<String,String> expectedParameters = new HashMap<>();
        expectedParameters.put("authentication-provider", "org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider");
        FilterRegistrationBean testBean = securityFilter.restFilterRegistrationBean();
        assertEquals(expectedParameters, testBean.getInitParameters());
    }
}
