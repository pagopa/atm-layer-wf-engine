package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitStateListenerStart implements TaskListener {

    private RedisClient redisClient;

    private TaskDefinition taskDefinition;


    @Override
    public void notify(DelegateTask delegateTask) {

        log.info("DelegateTask : id = {} , processInstanceId = {} , processDefinitionId = {} , TenantId = {} , Priority = {} , TaskDefinitionKey = {}",
                delegateTask.getId(), delegateTask.getProcessInstanceId(), delegateTask.getProcessDefinitionId(), delegateTask.getTenantId(), delegateTask.getPriority(), delegateTask.getTaskDefinitionKey());

        DelegateExecution execution = delegateTask.getExecution();
        log.info("getExecution() : BusinessKey = {} , currentActivityName = {} , currentActivityId = {} , parentId = {} , ActivityInstanceId = {} , CurrentTransitionId = {} , ProcessDefinitionId = {}, Id = {}",
                execution.getBusinessKey(), execution.getCurrentActivityName(), execution.getCurrentActivityId(),execution.getParentId(),
                execution.getActivityInstanceId(), execution.getCurrentTransitionId(), execution.getParentActivityInstanceId(), execution.getId());

        Task task = populateTask(delegateTask,taskDefinition);
        log.info(" task: {}", task);

        redisClient.publish(delegateTask.getExecution().getBusinessKey(), task);
    }

    private Task populateTask(DelegateTask delegateTask, TaskDefinition taskDefinition) {
        Task task = new Task();
        task.setId(delegateTask.getId());
        task.setVariables(delegateTask.getVariables());

        if (taskDefinition != null) {

            RuntimeService runtimeService = delegateTask.getProcessEngineServices().getRuntimeService();

            String processInstanceId = delegateTask.getProcessInstanceId();
            if (processInstanceId != null) {
                String formKey = (String) runtimeService.getVariable(processInstanceId, "camunda:formKey");
                log.info("FormKey = {}", formKey);
                task.setForm(formKey);
            }
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
