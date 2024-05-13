package it.pagopa.wf.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VerifyResponseTest {
    @Test
    void testDataAnnotation() {
        VerifyResponse testResponse = new VerifyResponse();
        testResponse.setIsVerified(Boolean.TRUE);
        testResponse.setMessage("test message");
        assertEquals(Boolean.TRUE, testResponse.getIsVerified());
        assertEquals("test message", testResponse.getMessage());
        assertEquals("VerifyResponse(isVerified=true, message=test message)", testResponse.toString());
    }
}
