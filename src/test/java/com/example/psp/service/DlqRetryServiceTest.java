package com.example.psp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DlqRetryServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DlqRetryService dlqRetryService;

    @Test
    void retryMessagesMovesRequestedCount() {
        dlqRetryService.setDlqQueue("webhook-dlq-queue");

        Message message = mock(Message.class);
        when(rabbitTemplate.receive("webhook-dlq-queue"))
                .thenReturn(message)
                .thenReturn(message)
                .thenReturn(null);

        int moved = dlqRetryService.retryMessages(5, "webhook-queue");

        assertEquals(2, moved);
        verify(rabbitTemplate, times(2)).send("", "webhook-queue", message);
    }

    @Test
    void retryMessagesStopsWhenDlqEmpty() {
        dlqRetryService.setDlqQueue("webhook-dlq-queue");

        when(rabbitTemplate.receive("webhook-dlq-queue")).thenReturn(null);

        int moved = dlqRetryService.retryMessages(3, "webhook-queue");

        assertEquals(0, moved);
        verify(rabbitTemplate, never()).send(anyString(), anyString(), any(Message.class));
    }
}
