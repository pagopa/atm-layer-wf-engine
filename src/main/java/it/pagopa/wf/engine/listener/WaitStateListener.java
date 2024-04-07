package it.pagopa.wf.engine.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class WaitStateListener implements ExecutionListener {


    private final String notificationCallbackBasePath;

    public WaitStateListener(String notificationCallbackBasePath) {
        this.notificationCallbackBasePath = notificationCallbackBasePath;
    }

    private final static String CALLBACK_CONTEXT_PATH = "/workflow/listener/notify/{requestId}";

    @Override
    public void notify(DelegateExecution execution) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        String apiUrl = notificationCallbackBasePath + CALLBACK_CONTEXT_PATH;
        String requestId = (String) execution.getVariable("requestId");
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityName = execution.getCurrentActivityName();
        String currentActivityId = execution.getCurrentActivityId();
        String processDefinitionId = execution.getProcessDefinitionId();
        Notification notification = Notification.builder()
                .processInstanceId(processInstanceId)
                .processDefinitionId(processDefinitionId)
                .currentActivityName(currentActivityName)
                .requestId(requestId)
                .currentActivityId(currentActivityId)
                .requestId(requestId).build();
        HttpEntity<Notification> requestEntity = new HttpEntity<>(notification, headers);
        String[] params = {requestId};

        ResponseEntity<Void> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                Void.class,
                (Object[]) params);

        // Gestione della risposta
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Wait state notified for process with requestId: {}", requestId);
        } else {
            log.error("Failed to notify wait state for process with requestId: {} with status code: {}", requestId, response.getStatusCode());
        }
    }
}
