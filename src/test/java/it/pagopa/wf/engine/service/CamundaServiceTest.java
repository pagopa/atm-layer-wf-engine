package it.pagopa.wf.engine.service;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CamundaServiceTest {

    @Mock
    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @InjectMocks
    private CamundaService camundaService;

    @BeforeEach
    void setUp() {
        JuelExpressionManager expressionManager = new JuelExpressionManager();
        when(processEngineConfiguration.getExpressionManager()).thenReturn(expressionManager);
    }

    private MultipartFile getMultipartFileFromResource(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = resource.getInputStream();
        when(file.getInputStream()).thenReturn(inputStream);
        when(file.getName()).thenReturn(resource.getFilename());
        return file;
    }

    @Test
    void testValidateFile_validBpmn() throws IOException {
        MultipartFile file = getMultipartFileFromResource("Test.bpmn");

        VerifyResponse response = camundaService.validateFile(file);

        assertTrue(response.getIsVerified());
        assertEquals("Correct Bpmn", response.getMessage());
    }

    @Test
    void testValidateFile_nonexecutableBpmn() throws IOException {
        MultipartFile file = getMultipartFileFromResource("Test1.bpmn");

        VerifyResponse response = camundaService.validateFile(file);

        assertFalse(response.getIsVerified());
        assertEquals("non-executable process. Set the attribute isExecutable=true to deploy this process.", response.getMessage());
    }

    @Test
    void testValidateFile_HTTLnull() throws IOException {
        MultipartFile file = getMultipartFileFromResource("Test2.bpmn");

        VerifyResponse response = camundaService.validateFile(file);

        assertFalse(response.getIsVerified());
        assertEquals("non-executable process. History Time To Live cannot be null.", response.getMessage());
    }


}
