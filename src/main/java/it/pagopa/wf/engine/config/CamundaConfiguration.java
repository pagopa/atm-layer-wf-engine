package it.pagopa.wf.engine.config;

import it.pagopa.wf.engine.parselistener.CustomUserTaskStartParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "engine-notification.wait-status", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CamundaConfiguration {
    
    @Autowired
    CustomUserTaskStartParseListener customUserTaskStartParseListener;

    @Autowired
    public void configureProcessEngineConfiguration(ProcessEngineConfigurationImpl config) {
        config.getCustomPostBPMNParseListeners().add(customUserTaskStartParseListener);
    }
}
