package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.Tracer;
import it.pagopa.wf.engine.model.ParentSpanContext;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("tracerStart")
public class TracerDelegateStart implements ExecutionListener {

    @Autowired
    Tracer tracer;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        Span activeSpan = Span.current();
        ParentSpanContext parentSpanContext = new ParentSpanContext();
        if (activeSpan != null) {
            SpanContext parentContext = activeSpan.getSpanContext();
            parentSpanContext.setTraceId(parentContext.getTraceId());
            parentSpanContext.setSpanId(parentContext.getSpanId());

        } else {
            Span newParentSpan = tracer.spanBuilder("Tracer Delegate").startSpan();
            parentSpanContext.setTraceId(newParentSpan.getSpanContext().getTraceId());
            parentSpanContext.setSpanId(newParentSpan.getSpanContext().getSpanId());
            newParentSpan.end();
        }
        delegateExecution.setVariable("activityParentSpan",objectMapper.writeValueAsString(parentSpanContext));
    }
}
