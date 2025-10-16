package com.example.psp.consumer;

import com.example.psp.dto.WebhookRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebhookConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleMessage(@Payload WebhookRequestDTO message) {
        getLogger().info("Received message from queue: {}", message.getTransactionId());

        try {
            getLogger().info("Processing payment for transaction ID: {}", message.getTransactionId());
            getLogger().info("Status: {}, Amount: {} {}", message.getPaymentStatus(), message.getAmount(), message.getCurrency());

            if ("FAIL".equalsIgnoreCase(message.getPaymentStatus())) {
                throw new RuntimeException("Simulating a processing failure for transaction: " + message.getTransactionId());
            }

            Thread.sleep(1000);

            getLogger().info("Successfully processed message for transaction ID: {}", message.getTransactionId());

        } catch (Exception e) {
            getLogger().error("Error processing message for transaction ID {}: {}", message.getTransactionId(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected Logger getLogger() {
        return log;
    }
}
