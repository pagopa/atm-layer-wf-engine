package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import it.pagopa.wf.engine.controller.CamundaController;
import it.pagopa.wf.engine.model.ParentSpanContext;
import it.pagopa.wf.engine.service.CamundaService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TracerDelegateStartTest {

    @Mock
    Tracer tracer;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    TracerDelegateStart tracerDelegateStart;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotifySpanNotNull() throws Exception {
        SpanBuilder mockSpanBuilder = mock(SpanBuilder.class);
        Span mockSpan = mock(Span.class);
        SpanContext mockSpanContext = mock(SpanContext.class);

        when(tracer.spanBuilder(any(String.class))).thenReturn(mockSpanBuilder);
        when(mockSpanBuilder.startSpan()).thenReturn(mockSpan);
        when(mockSpanContext.getTraceId()).thenReturn("traceId");
        when(mockSpanContext.getSpanId()).thenReturn("spanId");

        when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
        doNothing().when(mockSpan).end();

        when(objectMapper.writeValueAsString(any(ParentSpanContext.class))).thenReturn("valueAsString");
        DelegateExecution testDelegateExecution = mock(DelegateExecution.class);
        tracerDelegateStart.notify(testDelegateExecution);
    }

}
