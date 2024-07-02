package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import it.pagopa.wf.engine.model.ParentSpanContext;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("tracerWaitMessageStart")
public class TracerDelegateWaitMessageStart implements ExecutionListener {

    @Autowired
    Tracer tracer;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void notify(DelegateExecution delegateExecution) throws JsonProcessingException {

        String parent = (String) delegateExecution.getVariable("activityParentSpan");

        ParentSpanContext parentSpanContext = objectMapper.readValue(parent,ParentSpanContext.class);

        // ParentSpanContext parentSpanContext = objectMapper.readValue((String) delegateExecution.getVariable("processSpan2"), ParentSpanContext.class);
        SpanContext parentContext = SpanContext.createFromRemoteParent(parentSpanContext.getTraceId(), parentSpanContext.getSpanId(), TraceFlags.getSampled(), TraceState.getDefault());
        Context context = Context.current().with(Span.wrap(parentContext));
        try (Scope scope = context.makeCurrent()) {
            tracer.spanBuilder("Start Wait Message").setParent(context).startSpan().end();
        }

    }
}
