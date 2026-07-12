package com.system.application.shared.services.cache;

import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface CacheService {
    <T> Optional<T> get(String key, TypeReference<T> typeReference);
    void set(String key, Object value, Duration ttl);
    void delete(String key);
    void deleteAll(List<String> keys);
    boolean exists(String key);
    void extendTtl(String key, Duration newTtl);
    void evictByPattern(String pattern); // Apaga todo um pattern com SCAN
}