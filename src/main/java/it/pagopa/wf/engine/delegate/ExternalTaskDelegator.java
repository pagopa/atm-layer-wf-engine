package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.wf.engine.service.CallRestService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("externalTaskDelegator")
@Slf4j
public class ExternalTaskDelegator implements JavaDelegate {


    @Autowired
    CallRestService callRestService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        Map<String, Object> variables = delegateExecution.getVariables();
        log.info("--- Call Adapter with variables {}: and businessKey: {} ",variables, delegateExecution.getBusinessKey());
        delegateExecution.setVariables(callRestService.callAdapter(variables));
    }
}
