package com.example.psp.service;

import com.example.psp.dto.WebhookRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final long EXPIRATION_MINUTES = 60; // Or a suitable duration

    private final StringRedisTemplate redisTemplate;

    public boolean isRequestProcessed(String idempotencyKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(IDEMPOTENCY_KEY_PREFIX + idempotencyKey));
    }

    public void markRequestAsProcessed(String idempotencyKey) {
        redisTemplate.opsForValue().set(
                IDEMPOTENCY_KEY_PREFIX + idempotencyKey,
                "processed",
                EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }
}
