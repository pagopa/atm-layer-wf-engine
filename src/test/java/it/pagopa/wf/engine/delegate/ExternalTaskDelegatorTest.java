package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.wf.engine.service.CallRestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExternalTaskDelegatorTest {

    @Mock
    CallRestService callRestService;

    @Mock
    private DelegateExecution delegateExecution;

    @InjectMocks
    ExternalTaskDelegator externalTaskDelegator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() throws JsonProcessingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        variables.put("key2", "value2");

        when(delegateExecution.getVariables()).thenReturn(variables);
        when(delegateExecution.getBusinessKey()).thenReturn("businessKey123");

        externalTaskDelegator.execute(delegateExecution);

        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(callRestService, times(1)).callAdapter(variablesCaptor.capture());

        Map<String, Object> capturedVariables = variablesCaptor.getValue();
        assertEquals(variables, capturedVariables);

        verify(delegateExecution, times(1)).getVariables();
        verify(delegateExecution, times(1)).getBusinessKey();
    }
}
