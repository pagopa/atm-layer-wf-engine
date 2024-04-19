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
import org.camunda.bpm.engine.form.TaskFormData;
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
            String formKey;
            TaskFormData taskFormData = delegateTask.getProcessEngineServices().getFormService().getTaskFormData(delegateTask.getId());
            if (taskFormData != null) {
                formKey = taskFormData.getFormKey();
                log.info("FormKey = {}", formKey);
                task.setForm(formKey);
            }
            task.setPriority(delegateTask.getPriority());
        }
        return task;
    }
}
