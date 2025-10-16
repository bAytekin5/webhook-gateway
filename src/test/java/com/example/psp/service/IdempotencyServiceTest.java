package com.example.psp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IdempotencyServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private IdempotencyService idempotencyService;

    @BeforeEach
    void setUp() {
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
        valueOperations = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        idempotencyService = new IdempotencyService(redisTemplate);
    }

    @Test
    void returnsTrueWhenKeyExists() {
        when(redisTemplate.hasKey("idempotency:txn-123"))
                .thenReturn(true);

        assertTrue(idempotencyService.isRequestProcessed("txn-123"));
    }

    @Test
    void returnsFalseWhenKeyMissing() {
        when(redisTemplate.hasKey("idempotency:txn-456"))
                .thenReturn(false);

        assertFalse(idempotencyService.isRequestProcessed("txn-456"));
    }

    @Test
    void marksRequestAsProcessed() {
        idempotencyService.markRequestAsProcessed("txn-789");

        verify(valueOperations).set(eq("idempotency:txn-789"), eq("processed"), anyLong(), eq(java.util.concurrent.TimeUnit.MINUTES));
    }
}

