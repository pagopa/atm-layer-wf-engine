package it.pagopa.wf.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.config.RedisProperty;
import it.pagopa.wf.engine.model.Task;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
@Data
public class WaitStateListener implements ExecutionListener {

    @Autowired
    RedisProperty redisProperty;

    @Override
    public void notify(DelegateExecution execution) {

        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityName = execution.getCurrentActivityName();
        String currentActivityId = execution.getCurrentActivityId();
        String processDefinitionId = execution.getProcessDefinitionId();

        log.info(" execution = " + execution);
        log.info(" BusinessKey = " + execution.getBusinessKey());
        log.info(" processInstanceId " + processInstanceId +" currentActivityName " +currentActivityName +" currentActivityId " +currentActivityId +" processDefinitionId "+ processDefinitionId);
        Task task = new Task();
        task.setId(execution.getCurrentActivityId());
        //todo
        task.setVariables(execution.getVariables());
        task.setForm("formKey");
        task.setPriority(1);

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
