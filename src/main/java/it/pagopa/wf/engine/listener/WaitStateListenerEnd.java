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
import redis.clients.jedis.Jedis;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaitStateListenerEnd implements ExecutionListener {

    private RedisProperty redisProperty;

    @Override
    public void notify(DelegateExecution execution) {

        Task task = new Task();
        if(execution.getParentId().isBlank()) {
            try (Jedis jedis = new Jedis(redisProperty.getRedisHost(), redisProperty.getRedisPort())) {
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
}
