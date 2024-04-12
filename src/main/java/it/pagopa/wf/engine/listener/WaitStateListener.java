package it.pagopa.wf.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;

import java.util.Map;

@Slf4j
public class WaitStateListener implements ExecutionListener {

    @Value("spring.redis.host")
    private String redisHost;

    @Value("spring.redis.port")
    private int redisPort;

    @Override
    public void notify(DelegateExecution execution) {

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

        Task task = new Task();
        task.setId(requestId);
        task.setVariables(Map.of("key1", "value1", "key2", "value2"));
        task.setForm("formKey");
        task.setPriority(1);

        try (Jedis jedis = new Jedis(redisHost, redisPort)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String taskJson = objectMapper.writeValueAsString(task);

            jedis.publish(requestId, taskJson);
            log.info("Messaggio pubblicato con successo sul topic.");
        } catch (Exception e) {
            log.error("Failed to notify wait state for process with requestId: {}", requestId);
            e.printStackTrace();
        }

    }
}
