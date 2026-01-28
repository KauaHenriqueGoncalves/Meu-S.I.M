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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class PublicRateLimitConfig extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // só aplica em endpoints públicos
        if (!uri.startsWith("/public") &&
            !uri.startsWith("/auth") &&
            !uri.equals("/users/school-admin")
        ) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = request.getRemoteAddr();
        String key = ip + ":" + uri;
        Bucket bucket = buckets.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.classic(
                                10, // 10 requisições
                                Refill.intervally(10, Duration.ofMinutes(5))
                        ))
                        .build()
        );
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        }
        else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "error": "RATE_LIMIT_EXCEEDED",
                  "message": "Muitas tentativas, espere 5 minutos"
                }
            """);
        }
    }
}
