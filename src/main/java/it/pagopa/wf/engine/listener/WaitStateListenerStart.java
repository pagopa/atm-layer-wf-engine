package it.pagopa.wf.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.config.RedisProperty;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitStateListenerStart implements ExecutionListener {

    private RedisClient redisClient;

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
        log.info(" executionId =" + execution.getId());
        log.info(" getParentId " + execution.getParentId());
        log.info(" getActivityInstanceId " + execution.getActivityInstanceId());
        log.info(" getTenantId " + execution.getTenantId());
        log.info(" getCurrentTransitionId " + execution.getCurrentTransitionId());
        log.info(" getProcessDefinitionId " + execution.getParentActivityInstanceId());

        Task task = populateTask(execution,taskDefinition);
        log.info(" task: {}", task);

        redisClient.publish(execution.getBusinessKey(), task);
    }

    private Task populateTask(DelegateExecution delegateExecution, TaskDefinition taskDefinition) {
        Task task = new Task();
        task.setId(delegateExecution.getProcessInstanceId());
        task.setVariables(delegateExecution.getVariables());

        if (taskDefinition != null) {
            task.setForm(taskDefinition.getFormKey() == null ? null : taskDefinition.getFormKey().getExpressionText());
            int priority = 0;
            try {
                priority = taskDefinition.getPriorityExpression() == null ? 0 : Integer.parseInt(taskDefinition.getPriorityExpression().getExpressionText());
            } catch (Exception e) {
                log.error("priority not a number");
            }
            task.setPriority(priority);
        }
        return task;
    }

}