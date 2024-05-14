package it.pagopa.wf.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParentSpanContextTest {
    @Test
    void testAnnotations() {
        ParentSpanContext testSpan1 = new ParentSpanContext();
        testSpan1.setSpanId("spanId");
        testSpan1.setTraceId("traceId");
        ParentSpanContext testSpan2 = new ParentSpanContext("traceId", "spanId");
        ParentSpanContext testSpan3 = new ParentSpanContext("differentTraceId", "differentSpanId");
        assertEquals(testSpan2.getSpanId(), testSpan1.getSpanId());
        assertEquals(testSpan2.getTraceId(), testSpan1.getTraceId());
        assertEquals(testSpan2.toString(), testSpan1.toString());
        assertEquals(testSpan1.hashCode(), testSpan2.hashCode());
        assertTrue(testSpan1.equals(testSpan2));
        assertFalse(testSpan3.equals(testSpan1));
        assertFalse(testSpan3.equals("different object type"));
        assertTrue(testSpan1.canEqual(testSpan2));
        testSpan1.setTraceId("differentTraceId");
        assertFalse(testSpan1.equals(testSpan2));
        assertNotEquals(testSpan1.hashCode(), testSpan2.hashCode());
    }
}
