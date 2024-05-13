package it.pagopa.wf.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParentSpanContextTest {
    @Test
    void testAnnotations() {
        ParentSpanContext testSpan1 = new ParentSpanContext();
        testSpan1.setSpanId("spanId");
        testSpan1.setTraceId("traceId");
        ParentSpanContext testSpan2 = new ParentSpanContext("traceId", "spanId");
        assertEquals(testSpan2.getSpanId(), testSpan1.getSpanId());
        assertEquals(testSpan2.getTraceId(), testSpan1.getTraceId());
        assertEquals(testSpan2.toString(), testSpan1.toString());
        assertEquals(testSpan1.hashCode(), testSpan2.hashCode());
    }
}
