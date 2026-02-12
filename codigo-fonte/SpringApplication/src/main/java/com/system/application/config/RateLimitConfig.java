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
public class RateLimitConfig extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // Informe as rotas com rate limit
        Boolean endPointWithRateLimit = !uri.startsWith("/public") &&
                                        !uri.startsWith("/auth") &&
                                        !uri.equals("/users/school-admin");

        if (endPointWithRateLimit) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = request.getRemoteAddr();
        String key = ip + ":" + uri;
        Bucket bucket = buckets.computeIfAbsent(key, k ->
                Bucket.builder()
                        .addLimit(Bandwidth.classic(
                                10, // 10 requisicoes
                                Refill.intervally(10, Duration.ofMinutes(5)) // 5 minutos
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
