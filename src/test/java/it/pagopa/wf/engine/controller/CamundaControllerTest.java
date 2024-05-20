package it.pagopa.wf.engine.controller;

import it.pagopa.wf.engine.model.VerifyResponse;
import it.pagopa.wf.engine.service.CamundaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CamundaControllerTest {
    @Mock
    CamundaService camundaService;
    @InjectMocks
    CamundaController camundaController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerifyBpmn() throws IOException {
        MultipartFile testFile = mock(MultipartFile.class);
        VerifyResponse positiveResponse = new VerifyResponse();
        positiveResponse.setIsVerified(Boolean.TRUE);
        positiveResponse.setMessage("test passed");
        when(camundaService.validateFile(any(MultipartFile.class))).thenReturn(positiveResponse);
        VerifyResponse testResponse = camundaController.verifyBpmn(testFile);
        assertEquals(Boolean.TRUE, testResponse.getIsVerified());
        assertEquals("test passed", testResponse.getMessage());
    }
}
