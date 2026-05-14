
package com.qzer.scheduler.modules.monitor.service;

import com.qzer.scheduler.common.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final DataSource dataSource;
    private final RedisTemplate&lt;String, Object&gt; redisTemplate;

    public Map&lt;String, Object&gt; checkHealth() {
        Map&lt;String, Object&gt; health = new HashMap&lt;&gt;();
        health.put("status", "UP");
        health.put("database", checkDatabase());
        health.put("redis", checkRedis());
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    private Map&lt;String, Object&gt; checkDatabase() {
        Map&lt;String, Object&gt; result = new HashMap&lt;&gt;();
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(1);
            result.put("status", valid ? "UP" : "DOWN");
            result.put("details", "Database connection is " + (valid ? "healthy" : "unhealthy"));
        } catch (Exception e) {
            log.error("数据库健康检查失败", e);
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }

    private Map&lt;String, Object&gt; checkRedis() {
        Map&lt;String, Object&gt; result = new HashMap&lt;&gt;();
        try {
            String testKey = "health:test";
            redisTemplate.opsForValue().set(testKey, "ok");
            Object value = redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            result.put("status", "ok".equals(value) ? "UP" : "DOWN");
            result.put("details", "Redis connection is healthy");
        } catch (Exception e) {
            log.error("Redis健康检查失败", e);
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }
}

