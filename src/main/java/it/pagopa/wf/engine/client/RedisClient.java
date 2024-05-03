package it.pagopa.wf.engine.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Slf4j
@Component
public class RedisClient {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    public void publish(String key, Object object) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(redisHost, redisPort);
            ObjectMapper objectMapper = new ObjectMapper();
            String taskJson = objectMapper.writeValueAsString(object);
            log.info("Bkey = {}, taskJson = {}", key, taskJson);
            jedis.publish(key, taskJson);
            log.info("Messaggio pubblicato con successo sul topic.");
        } catch (Exception e) {
            log.error("Failed to notify wait state for process");
            e.printStackTrace();
        }
        finally {
            if(jedis!=null)
                jedis.close();
        }

    }
}
