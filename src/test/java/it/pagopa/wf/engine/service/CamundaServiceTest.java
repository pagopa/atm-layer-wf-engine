package it.pagopa.wf.engine.service;

import it.pagopa.wf.engine.model.VerifyResponse;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.el.JuelExpressionManager;
import org.camunda.bpm.engine.impl.util.xml.Element;
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

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
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
        lenient().when(processEngineConfiguration.getExpressionManager()).thenReturn(expressionManager);
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

    @Test
    void testValidateFile_JavaCodeDetected() throws IOException {
        MultipartFile file = getMultipartFileFromResource("TestWithJavaCode.bpmn");

        VerifyResponse response = camundaService.validateFile(file);

        assertFalse(response.getIsVerified());
        assertEquals("Rilevato codice Java: some.java.Class", response.getMessage());
    }

    @Test
    void testValidateFile_DangerousScriptDetected() throws IOException {
        MultipartFile file = getMultipartFileFromResource("TestWithGroovyScript.bpmn");

        VerifyResponse response = camundaService.validateFile(file);

        assertFalse(response.getIsVerified());
        assertEquals("Rilevato codice Java: println 'Hello, World!'", response.getMessage());
    }

    @Test
    void testCheckScriptTaskForJava_JavascriptWithJavaReferences() {
        Element scriptTask = mock(Element.class);
        when(scriptTask.attribute("scriptFormat")).thenReturn("javascript");
        when(scriptTask.getText()).thenReturn("new java.io.File('test.txt');");

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> camundaService.checkScriptTaskForJava(scriptTask));

        assertEquals("Lo script JavaScript contiene riferimenti a codice Java non Consentito.", exception.getMessage());
    }

    @Test
    void testCheckScriptTaskForJava_JavascriptWithoutJavaReferences() {
        Element scriptTask = mock(Element.class);
        when(scriptTask.attribute("scriptFormat")).thenReturn("javascript");
        when(scriptTask.getText()).thenReturn("console.log('Hello, World!');");

        assertDoesNotThrow(() -> camundaService.checkScriptTaskForJava(scriptTask));
    }

    @Test
    void testCheckScriptTaskForJava_DangerousScriptGroovy() {
        Element scriptTask = mock(Element.class);
        when(scriptTask.attribute("scriptFormat")).thenReturn("groovy");

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
                () -> camundaService.checkScriptTaskForJava(scriptTask));

        assertEquals("Rilevato script in linguaggio pericoloso (Java o Groovy).", exception.getMessage());
    }

    @Test
    void testContainsJavaReferences_WithJavaReference() {
        String scriptContent = "new java.io.File('test.txt');";

        boolean result = camundaService.containsJavaReferences(scriptContent);

        assertTrue(result);
    }

    @Test
    void testContainsJavaReferences_WithoutJavaReference() {
        String scriptContent = "console.log('Hello, World!');";

        boolean result = camundaService.containsJavaReferences(scriptContent);

        assertFalse(result);
    }

    @Test
    void testContainsJavaReferences_MultipleJavaReferences() {
        String scriptContent = "System.out.println('test'); new java.lang.String();";

        boolean result = camundaService.containsJavaReferences(scriptContent);

        assertTrue(result);
    }
}
