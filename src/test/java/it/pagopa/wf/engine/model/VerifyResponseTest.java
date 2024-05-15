package it.pagopa.wf.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VerifyResponseTest {
    @Test
    void testDataAnnotation() {
        VerifyResponse testResponse1 = new VerifyResponse();
        testResponse1.setIsVerified(Boolean.TRUE);
        testResponse1.setMessage("test message");
        assertEquals(Boolean.TRUE, testResponse1.getIsVerified());
        assertEquals("test message", testResponse1.getMessage());
        assertEquals("VerifyResponse(isVerified=true, message=test message)", testResponse1.toString());
        VerifyResponse testResponse2 = new VerifyResponse();
        testResponse2.setIsVerified(Boolean.FALSE);
        testResponse2.setMessage("test message");
        assertFalse(testResponse1.equals(testResponse2));
        assertTrue(testResponse1.canEqual(testResponse2));
        assertNotEquals(testResponse1.hashCode(), testResponse2.hashCode());
    }
}
