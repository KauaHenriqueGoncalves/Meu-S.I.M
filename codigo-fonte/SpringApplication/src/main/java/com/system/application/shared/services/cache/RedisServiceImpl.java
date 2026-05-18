package com.system.application.shared.services.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.application.shared.dto.PageResponse;
import jakarta.persistence.QueryTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.*;

@Service
public class RedisServiceImpl implements CacheService {
    private static final Logger log =
            LoggerFactory.getLogger(RedisServiceImpl.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private volatile boolean redisAvailable = true;

    public RedisServiceImpl(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        if (!isAvailable()) return Optional.empty();

        try {
            log.info("Buscando item no cache. [key={}] [typeReference={}]",
                    key, typeReference.getType());
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Optional.empty();
            }
            T value = objectMapper.readValue(json, typeReference);
            markAsAvailable();
            return Optional.ofNullable(value);
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException  e) {
            markAsUnavailable("get", key, e);
            return Optional.empty();
        }
        catch (Exception e) {
            log.error("Erro ao converter lista do cache. [key={}] [type={}] [error={}]",
                    key, typeReference, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        if (!isAvailable()) return;

        if (isEmpty(value)) {
            log.warn("O valor do objeto é nulo, não será cacheado. [key={}] [value={}] [ttl={}]",
                    key, value, ttl);
            return;
        }

        try {
            log.info("Inserindo cache pela key. [key={}] [ttl={}]", key, ttl);
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl);
            markAsAvailable();
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("set", key, e);
        }
        catch (JsonProcessingException e) {
            log.error("Erro ao converter lista do cache. [key={}] [error={}]", key, e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        if (!isAvailable()) return;

        try {
            log.info("Removendo cache pela key. [key={}]", key);
            redisTemplate.delete(key);
            markAsAvailable();
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("set", key, e);
        }
    }

    @Override
    public void deleteAll(List<String> keys) {
        if (!isAvailable()) return;

        try {
            log.info("Removendo varios cache pelas keys. [keys={}]", keys);
            redisTemplate.delete(keys);
            markAsAvailable();
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("set", String.valueOf(keys), e);
        }
    }

    @Override
    public boolean exists(String key) {
        if (!isAvailable()) return false;

        try {
            log.info("Verificando se o cache pela key existe. [key={}]", key);
            markAsAvailable();
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("set", key, e);
            return false;
        }
    }

    @Override
    public void extendTtl(String key, Duration newTtl) {
        if (!isAvailable()) return;

        try {
            log.info("Extendendo tempo de cache na key. [key={}] [ttl={}]", key, newTtl);
            redisTemplate.expire(key, newTtl);
            markAsAvailable();
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("set", key, e);
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        if (!isAvailable()) return;

        log.info("Evicting cache by pattern. [pattern={}]", pattern);
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(100).build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            cursor.forEachRemaining(key -> {
                try {
                    redisTemplate.delete(key);
                    log.debug("Cache evicted. [key={}]", key);
                }
                catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
                    markAsUnavailable("evictByPattern", key, e);
                }
            });
            markAsAvailable();
        }
        catch (RedisSystemException | RedisConnectionFailureException | QueryTimeoutException e) {
            markAsUnavailable("evictByPattern", pattern, e);
        }

        log.info("Finalizado evict por pattern. [pattern={}]", pattern);
    }

    @Scheduled(fixedDelay = 50000)
    public void checkRedisHealth() {
        if (redisAvailable) return;

        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            flushAllCaches();
            markAsAvailable();
        }
        catch (Exception e) {
            log.warn("Redis ainda indisponível. Próxima tentativa em 30s. [error={}]", e.getMessage());
        }
    }

    private boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof List<?> l) return l.isEmpty();
        if (value instanceof Collection<?> c) return c.isEmpty();
        if (value instanceof HashMap<?, ?> h) return h.isEmpty();
        if (value instanceof Map<?, ?> m) return m.isEmpty();
        if (value.getClass().isArray()) return Array.getLength(value) == 0;
        if (value instanceof PageResponse<?> p) return p.content().isEmpty();

        log.debug("Tipo não mapeado no isEmpty, será cacheado. [type={}]", value.getClass().getName());
        return false;
    }

    private boolean isAvailable() {
        if (!redisAvailable) {
            log.warn("Redis está marcado como indisponível, operação de cache ignorada.");
            return false;
        }
        return true;
    }

    private void markAsUnavailable(String operation, String key, Exception e) {
        log.error("Redis indisponível durante [{}]. Desabilitando cache. [key={}] [error={}]",
                operation, key, e.getMessage());
        redisAvailable = false;
    }

    private void markAsAvailable() {
        if (!redisAvailable) {
            log.info("Redis voltou a ficar disponível.");
            redisAvailable = true;
        }
    }

    private void flushAllCaches() {
        try {
            redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
            log.info("Cache limpo após reconexão do Redis.");
        }
        catch (Exception e) {
            log.error("Erro ao limpar cache após reconexão. [error={}]", e.getMessage());
        }
    }
}
