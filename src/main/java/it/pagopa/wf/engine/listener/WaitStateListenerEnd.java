package it.pagopa.wf.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.client.RedisClient;
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

    private RedisClient redisClient;

    @Override
    public void notify(DelegateExecution execution) {

        Task task = new Task();
        if(execution.getParentId() == null || execution.getParentId().isBlank()) {
            redisClient.publish(execution.getBusinessKey(), task);
        }
    }
}
