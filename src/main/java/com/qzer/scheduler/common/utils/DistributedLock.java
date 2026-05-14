package com.qzer.scheduler.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "distributed:lock:";
    private static final long DEFAULT_WAIT_TIME = 3000;
    private static final long DEFAULT_LEASE_TIME = 30000;

    private final ThreadLocal<String> lockValue = new ThreadLocal<>();

    public boolean lock(String key) {
        return lock(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS);
    }

    public boolean lock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        String lockKey = LOCK_PREFIX + key;
        String value = UUID.randomUUID().toString();
        long waitMillis = timeUnit.toMillis(waitTime);
        long leaseMillis = timeUnit.toMillis(leaseTime);
        long deadline = System.currentTimeMillis() + waitMillis;

        while (System.currentTimeMillis() < deadline) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, value, leaseMillis, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE.equals(success)) {
                lockValue.set(value);
                return true;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public boolean unlock(String key) {
        String lockKey = LOCK_PREFIX + key;
        String value = lockValue.get();
        if (value == null) {
            return false;
        }

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), value);
        lockValue.remove();
        return result != null && result == 1;
    }

    public <T> T executeWithLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit, LockCallback<T> callback) {
        boolean locked = lock(key, waitTime, leaseTime, timeUnit);
        if (!locked) {
            throw new RuntimeException("获取分布式锁失败: " + key);
        }
        try {
            return callback.doInLock();
        } finally {
            unlock(key);
        }
    }

    public <T> T executeWithLock(String key, LockCallback<T> callback) {
        return executeWithLock(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.MILLISECONDS, callback);
    }

    @FunctionalInterface
    public interface LockCallback<T> {
        T doInLock();
    }
}
