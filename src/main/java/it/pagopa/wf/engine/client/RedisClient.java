package it.pagopa.wf.engine.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.wf.engine.config.RedisProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
public class RedisClient {
    @Autowired
    RedisProperty redisProperty;

    public void publish(String key, Object object) {
        try (Jedis jedis = new Jedis(redisProperty.getRedisHost(), redisProperty.getRedisPort())) {
            ObjectMapper objectMapper = new ObjectMapper();
            String taskJson = objectMapper.writeValueAsString(object);
            log.info("Bkey = {}, taskJson = {}", key, taskJson);
            jedis.publish(key, taskJson);
            log.info("Messaggio pubblicato con successo sul topic.");
        } catch (Exception e) {
            log.error("Failed to notify wait state for process");
            e.printStackTrace();
        }

    }
}
