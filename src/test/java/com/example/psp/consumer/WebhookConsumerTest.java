package com.example.psp.consumer;

import com.example.psp.dto.WebhookRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookConsumerTest {

    @Spy
    @InjectMocks
    private WebhookConsumer webhookConsumer;

    private WebhookRequestDTO request;

    @BeforeEach
    void setUp() {
        request = new WebhookRequestDTO();
        request.setTransactionId("txn-100");
        request.setPaymentStatus("SUCCESS");
        request.setAmount(10.0);
        request.setCurrency("USD");
    }

    @Test
    void handleMessageSuccessLogsProcessing() {
        Logger logger = mock(Logger.class);
        doReturn(logger).when(webhookConsumer).getLogger();

        webhookConsumer.handleMessage(request);

        verify(logger).info("Received message from queue: {}", request.getTransactionId());
        verify(logger).info("Successfully processed message for transaction ID: {}", request.getTransactionId());
        verify(logger, never()).error(anyString(), any(), any());
    }

    @Test
    void handleMessageFailureThrowsRuntimeException() {
        request.setPaymentStatus("FAIL");

        assertThrows(RuntimeException.class, () -> webhookConsumer.handleMessage(request));
    }
}

