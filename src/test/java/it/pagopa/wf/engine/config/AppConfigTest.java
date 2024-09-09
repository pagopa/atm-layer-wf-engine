package it.pagopa.wf.engine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
class AppConfigTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRestTemplateBean() {
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
    }

    @Test
    void testObjectMapperBean() {
        assertNotNull(objectMapper, "ObjectMapper bean should not be null");
    }

    @Test
    void testBeanCreation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            RestTemplate rt = context.getBean(RestTemplate.class);
            assertNotNull(rt, "RestTemplate bean should not be null");

            ObjectMapper om = context.getBean(ObjectMapper.class);
            assertNotNull(om, "ObjectMapper bean should not be null");
        }
    }
}
