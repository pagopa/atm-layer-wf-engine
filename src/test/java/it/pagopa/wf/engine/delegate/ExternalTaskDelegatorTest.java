package it.pagopa.wf.engine.delegate;

import it.pagopa.wf.engine.service.CallRestService;
import it.pagopa.wf.engine.util.HttpRequestUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
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
    void testExecute() {
        when(delegateExecution.getVariables()).thenReturn(Map.of("Authorization", "Bearer token"));
        when(delegateExecution.getBusinessKey()).thenReturn("businessKey123");

        HttpHeaders mockHeaders = new HttpHeaders();
        mockHeaders.set("Authorization", "Bearer token");

        try (MockedStatic<HttpRequestUtils> mockedHttpRequestUtils = mockStatic(HttpRequestUtils.class)) {
            mockedHttpRequestUtils.when(() -> HttpRequestUtils.createHttpHeaders(any())).thenReturn(mockHeaders);

            externalTaskDelegator.execute(delegateExecution);

            verify(callRestService).callAdapter(mockHeaders);
            verify(delegateExecution).getVariables();
            verify(delegateExecution).getBusinessKey();
            mockedHttpRequestUtils.verify(() -> HttpRequestUtils.createHttpHeaders(any()));
        }
    }
}
