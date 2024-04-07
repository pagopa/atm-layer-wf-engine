package it.pagopa.wf.engine.config;

import it.pagopa.wf.engine.parselistener.CustomUserTaskStartParseListener;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaConfiguration {

    @Autowired
    public void configureProcessEngineConfiguration(ProcessEngineConfigurationImpl config) {
        config.getCustomPostBPMNParseListeners().add(new CustomUserTaskStartParseListener());
    }
}
