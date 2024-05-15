package it.pagopa.wf.engine.service;

import camundajar.impl.scala.Array;
import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.camunda.bpm.engine.impl.util.xml.Namespace;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CamundaServiceTest {

    CamundaService camundaService = new CamundaService();
    @Test
    void testValidateFileException() throws IOException {
        MultipartFile testFile = mock(MultipartFile.class);
        VerifyResponse testResponse = camundaService.validateFile(testFile);
        assertEquals(Boolean.FALSE, testResponse.getIsVerified());
    }

    @Test
    void testCheckExecutable(){
        Element mockRootElement = mock(Element.class);
        List<Element> mockProcessElements = new ArrayList<>();
        mockProcessElements.add(mockRootElement);
        when(mockRootElement.elements("process")).thenReturn(mockProcessElements);
        String testResponse = "";
        try {
            camundaService.checkExecutable(mockRootElement);
        } catch (UnsupportedOperationException e){
            testResponse = "method failed";
        }
        assertEquals("method failed", testResponse);

        testResponse = "";
        when(mockRootElement.attribute("isExecutable")).thenReturn("false");
        try {
            camundaService.checkExecutable(mockRootElement);
        } catch (UnsupportedOperationException e){
            testResponse = "method failed";
        }
        assertEquals("method failed", testResponse);

        testResponse = "";
        when(mockRootElement.attribute("isExecutable")).thenReturn("true");
        when(mockRootElement.attributeNS(any(Namespace.class), any(String.class))).thenReturn("");
        try {
            camundaService.checkExecutable(mockRootElement);
        } catch (UnsupportedOperationException e){
            testResponse = "method failed";
        }
        assertEquals("method failed", testResponse);
    }
}
