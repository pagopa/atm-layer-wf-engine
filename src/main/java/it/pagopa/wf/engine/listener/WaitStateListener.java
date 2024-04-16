package it.pagopa.wf.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.config.RedisProperty;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import redis.clients.jedis.Jedis;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitStateListener implements ExecutionListener {

    private RedisProperty redisProperty;

    private TaskDefinition taskDefinition;

    @Override
    public void notify(DelegateExecution execution) {

        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityName = execution.getCurrentActivityName();
        String currentActivityId = execution.getCurrentActivityId();
        String processDefinitionId = execution.getProcessDefinitionId();

        log.info(" execution = " + execution);
        log.info(" BusinessKey = " + execution.getBusinessKey());
        log.info(" processInstanceId " + processInstanceId + " currentActivityName " + currentActivityName + " currentActivityId " + currentActivityId + " processDefinitionId " + processDefinitionId);

        log.info("TaskDefinition = " + taskDefinition.toString() +
                "\n FormKey = " + (taskDefinition.getFormKey() == null ? "is null" : taskDefinition.getFormKey().getExpressionText()) +
                "\n Priority = " + (taskDefinition.getPriorityExpression() == null ? "is null" : taskDefinition.getPriorityExpression().getExpressionText()));

        Task task = new Task();
        task.setId(execution.getCurrentActivityId());
        task.setVariables(execution.getVariables());
        task.setForm(taskDefinition.getFormKey() == null ? null : taskDefinition.getFormKey().getExpressionText());
        int priority = 0;
        try {
            priority = taskDefinition.getPriorityExpression() == null ? 0 : Integer.parseInt(taskDefinition.getPriorityExpression().getExpressionText());
        } catch (Exception e) {
            log.error("priority not a number");
        }
        task.setPriority(priority);

        try (Jedis jedis = new Jedis("pagopa-dev-atm-layer-redis.vab4cc.ng.0001.eus1.cache.amazonaws.com", 6379)) {
            ObjectMapper objectMapper = new ObjectMapper();
            String taskJson = objectMapper.writeValueAsString(task);

            jedis.publish(execution.getBusinessKey(), taskJson);
            log.info("Messaggio pubblicato con successo sul topic.");
        } catch (Exception e) {
            log.error("Failed to notify wait state for process");
            e.printStackTrace();
        }

    }
}
