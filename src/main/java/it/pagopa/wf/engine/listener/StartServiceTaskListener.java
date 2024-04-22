package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.task.TaskDefinition;

@Slf4j
@Data
@AllArgsConstructor
public class StartServiceTaskListener implements ExecutionListener {

    private RedisClient redisClient;

    @Override
    public void notify(DelegateExecution execution) {

        Task task = new Task();
        task.setExternal(true);
        log.info( "External task BusinessKey: {}",execution.getBusinessKey());
        redisClient.publish(execution.getBusinessKey(), task);
        }
    }

