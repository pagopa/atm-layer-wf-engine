package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.wf.engine.service.CallRestService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.VariableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ExternalTaskDelegatorTest {

    @Mock
    private CallRestService callRestService;

    @Mock
    private DelegateExecution delegateExecution;

    @InjectMocks
    private ExternalTaskDelegator externalTaskDelegator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute() throws JsonProcessingException {

        Map<String, Object> variables = new HashMap<>();
        variables.put("key1", "value1");
        when(delegateExecution.getVariables()).thenReturn(variables);
        when(delegateExecution.getBusinessKey()).thenReturn("testBusinessKey");

        VariableMap responseVariables = mock(VariableMap.class);
        when(callRestService.callAdapter(variables)).thenReturn(responseVariables);

        externalTaskDelegator.execute(delegateExecution);

        verify(delegateExecution).getVariables();
        verify(delegateExecution).getBusinessKey();
        verify(callRestService).callAdapter(variables);
        verify(delegateExecution).setVariables(responseVariables);
    }
}
