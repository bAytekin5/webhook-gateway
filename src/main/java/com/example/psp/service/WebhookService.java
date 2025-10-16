package com.example.psp.service;

import com.example.psp.dto.WebhookRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    public void processWebhook(WebhookRequestDTO request) {
        log.info("Sending webhook to queue: {}", request.getTransactionId());
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, request);
            log.info("Successfully sent webhook to queue: {}", request.getTransactionId());
        } catch (Exception e) {
            log.error("Failed to send webhook to queue: {}", request.getTransactionId(), e);
            // Depending on requirements, you might want to re-throw or handle it differently
            throw new RuntimeException("Error sending message to queue", e);
        }
    }
}
