package com.system.application.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // limite mais restrito (alvo de brute force)
    private static final List<String> PUBLIC_RATE_LIMITED_PATHS =
            List.of(
                    "/api/v1/auth/login",
                    "/api/v1/auth/login/admin",
                    "/api/v1/auth/refresh",
                    "/api/v1/school-admins"
            );

    // limite mais generoso
    private static final List<String> PRIVATE_RATE_LIMITED_PATHS =
            List.of(
                    "/api/v1/students"
            );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        boolean isPublicLimited = PUBLIC_RATE_LIMITED_PATHS.stream()
                .anyMatch(uri::startsWith);

        boolean isPrivateLimited = PRIVATE_RATE_LIMITED_PATHS.stream()
                .anyMatch(uri::startsWith);

        if (!isPublicLimited && !isPrivateLimited) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        String key = ip + ":" + uri;

        Bucket bucket = buckets.computeIfAbsent(key, k ->
                isPublicLimited ? buildPublicBucket() : buildPrivateBucket()
        );

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "RATE_LIMIT_EXCEEDED",
                  "message": "Muitas tentativas, tente novamente mais tarde"
                }
            """);
        }
    }

    // 10 requisições a cada 5 minutos por IP+rota
    private Bucket buildPublicBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        10,
                        Refill.intervally(10, Duration.ofMinutes(5))
                ))
                .build();
    }

    // 100 requisições a cada 1 minuto por IP+rota
    private Bucket buildPrivateBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        100,
                        Refill.intervally(100, Duration.ofMinutes(1))
                ))
                .build();
    }
}
