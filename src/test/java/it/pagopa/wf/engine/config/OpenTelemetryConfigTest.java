package it.pagopa.wf.engine.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OpenTelemetryConfigTest {

    OpenTelemetryConfig openTelemetryConfig = new OpenTelemetryConfig();
    @Test
    void testSpanExporter() {
        Assertions.assertDoesNotThrow(() -> openTelemetryConfig.otlpHttpSpanExporter("http://test/tracing/url"));
    }


}
