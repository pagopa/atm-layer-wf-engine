package it.pagopa.wf.engine.config;

import org.junit.jupiter.api.Test;

public class OpenTelemetryConfigTest {

    OpenTelemetryConfig openTelemetryConfig = new OpenTelemetryConfig();
    @Test
    void testSpanExporter(){
        openTelemetryConfig.otlpHttpSpanExporter("http://test/tracing/url");
    }
}
