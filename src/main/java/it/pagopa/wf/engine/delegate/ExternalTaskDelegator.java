package it.pagopa.wf.engine.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.service.CallRestService;
import it.pagopa.wf.engine.util.HttpRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service("externalTaskDelegator")
@Slf4j
public class ExternalTaskDelegator implements JavaDelegate {


    @Autowired
    CallRestService callRestService;


    @Override
    public void execute(DelegateExecution delegateExecution) {
        HttpHeaders httpHeaders = HttpRequestUtils.createHttpHeaders(delegateExecution.getVariables());
        log.info("--- Call Adapter with headers {}: and businessKey: {} ",httpHeaders, delegateExecution.getBusinessKey());
        callRestService.callAdapter(httpHeaders);
    }
}
