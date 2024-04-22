package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

@Slf4j
@Data
@AllArgsConstructor
public class StartServiceTaskListener implements TaskListener {

    private RedisClient redisClient;

    private TaskDefinition taskDefinition;
    @Override
    public void notify(DelegateTask delegateTask) {
        Task serviceTask = populateServiceTask(delegateTask,taskDefinition);
        log.info(" ServiceTask: {}", serviceTask);
        redisClient.publish(delegateTask.getExecution().getBusinessKey(), serviceTask);
    }

    private Task populateServiceTask(DelegateTask delegateTask, TaskDefinition taskDefinition) {
        Task serviceTask = new Task();
        serviceTask.setId(delegateTask.getId());
        serviceTask.setVariables(delegateTask.getVariables());
        serviceTask.setExternal(true);

        if (taskDefinition != null) {
            String formKey;
            TaskFormData taskFormData = delegateTask.getProcessEngineServices().getFormService().getTaskFormData(delegateTask.getId());
            if (taskFormData != null) {
                formKey = taskFormData.getFormKey();
                serviceTask.setForm(formKey);
            }
            serviceTask.setPriority(delegateTask.getPriority());
        }
        return serviceTask;
    }
}
