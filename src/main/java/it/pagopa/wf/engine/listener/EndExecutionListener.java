package it.pagopa.wf.engine.listener;

import it.pagopa.wf.engine.client.RedisClient;
import it.pagopa.wf.engine.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
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
        // Ottieni un riferimento al servizio di runtime di Camunda
        RuntimeService runtimeService = execution.getProcessEngine().getRuntimeService();
        long activeProcessInstances = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(execution.getBusinessKey())
                .active()
                .count();

        // Verifica se l'evento è di completamento del processo
        //if (ExecutionListener.EVENTNAME_END.equals(execution.getEventName())) {
        //if(execution.getParentId() == null || execution.getParentId().isBlank()) {
        if(activeProcessInstances==1)
            redisClient.publish(execution.getBusinessKey(), task);
    }
}
