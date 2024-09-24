package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import it.pagopa.wf.engine.model.ParentSpanContext;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class TracerDelegateWaitMessageStartTest {

    @Mock
    private Tracer tracer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DelegateExecution delegateExecution;

    @InjectMocks
    private TracerDelegateWaitMessageStart tracerDelegateWaitMessageStart;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotify() throws JsonProcessingException {

        String parentSpanJson = "{\"traceId\":\"trace-id\",\"spanId\":\"span-id\"}";
        when(delegateExecution.getVariable("activityParentSpan")).thenReturn(parentSpanJson);

        ParentSpanContext parentSpanContext = new ParentSpanContext();
        parentSpanContext.setTraceId("trace-id");
        parentSpanContext.setSpanId("span-id");
        when(objectMapper.readValue(parentSpanJson, ParentSpanContext.class)).thenReturn(parentSpanContext);

        SpanBuilder spanBuilder = mock(SpanBuilder.class);
        Span span = mock(Span.class);
//        when(tracer.spanBuilder("Start Wait Message")).thenReturn(spanBuilder);
//        when(spanBuilder.setParent(any(Context.class))).thenReturn(spanBuilder);
//        when(spanBuilder.startSpan()).thenReturn(span);

        tracerDelegateWaitMessageStart.notify(delegateExecution);

        verify(delegateExecution).getVariable("activityParentSpan");
        verify(objectMapper).readValue(parentSpanJson, ParentSpanContext.class);
//        verify(tracer).spanBuilder("Start Wait Message");
//        verify(spanBuilder).setParent(any(Context.class));
//        verify(spanBuilder).startSpan();
//        verify(span).end();
    }
}
