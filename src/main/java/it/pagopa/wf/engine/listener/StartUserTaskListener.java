package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartUserTaskListener implements TaskListener {

    private RedisClient redisClient;

    private TaskDefinition taskDefinition;


    @Override
    public void notify(DelegateTask delegateTask) {

//        log.info("DelegateTask : id = {} , processInstanceId = {} , processDefinitionId = {} , TenantId = {} , Priority = {} , TaskDefinitionKey = {}",
//                delegateTask.getId(), delegateTask.getProcessInstanceId(), delegateTask.getProcessDefinitionId(), delegateTask.getTenantId(), delegateTask.getPriority(), delegateTask.getTaskDefinitionKey());
//
//        DelegateExecution execution = delegateTask.getExecution();
//        log.info("getExecution() : BusinessKey = {} , currentActivityName = {} , currentActivityId = {} , parentId = {} , ActivityInstanceId = {} , CurrentTransitionId = {} , ProcessDefinitionId = {}, Id = {}",
//                execution.getBusinessKey(), execution.getCurrentActivityName(), execution.getCurrentActivityId(),execution.getParentId(),
//                execution.getActivityInstanceId(), execution.getCurrentTransitionId(), execution.getParentActivityInstanceId(), execution.getId());

        Task userTask = populateUserTask(delegateTask,taskDefinition);
        log.info("UserTask: {}", userTask);

        redisClient.publish(delegateTask.getExecution().getBusinessKey(), userTask);
    }

    public Task populateUserTask(DelegateTask delegateTask, TaskDefinition taskDefinition) {
        Task userTask = new Task();
        userTask.setId(delegateTask.getId());
        userTask.setVariables(delegateTask.getVariables());

        if (taskDefinition != null) {
            String formKey;
            TaskFormData taskFormData = delegateTask.getProcessEngineServices().getFormService().getTaskFormData(delegateTask.getId());
            if (taskFormData != null) {
                formKey = taskFormData.getFormKey();
                userTask.setForm(formKey);
            }
            userTask.setPriority(delegateTask.getPriority());
        }
        return userTask;
    }
}
