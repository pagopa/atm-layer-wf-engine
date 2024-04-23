package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndExecutionListener implements ExecutionListener {

    private RedisClient redisClient;

    @Override
    public void notify(DelegateExecution execution) {

        Task task = new Task();
        log.info("ENDEVENT with  parentId: {} and BusinessKey: {} and eventName: {}", execution.getParentId(), execution.getBusinessKey(),execution.getEventName());

        // Verifica se l'evento è di completamento del processo
        //if (ExecutionListener.EVENTNAME_END.equals(execution.getEventName())) {
        if(execution.getParentId() == null || execution.getParentId().isBlank()) {
            redisClient.publish(execution.getBusinessKey(), task);
        }
    }
}
