package it.pagopa.wf.engine.config;

import org.camunda.bpm.engine.rest.security.auth.ProcessEngineAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SecurityFilter {

    @Bean
    public FilterRegistrationBean<ProcessEngineAuthenticationFilter> restFilterRegistrationBean() {
        FilterRegistrationBean<ProcessEngineAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        ProcessEngineAuthenticationFilter customFilter = new ProcessEngineAuthenticationFilter();
        registrationBean.addInitParameter("authentication-provider", "org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider");
        registrationBean.setFilter(customFilter);
        registrationBean.addUrlPatterns("/engine-rest/*");
        registrationBean.setOrder(1); //set precedence
        return registrationBean;
    }
}
