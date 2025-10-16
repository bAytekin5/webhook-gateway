package com.example.psp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DlqRetryService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.dlq-queue}")
    private String dlqQueue;

    void setDlqQueue(String dlqQueue) {
        this.dlqQueue = dlqQueue;
    }

    public int retryMessages(int messageCount, String targetQueue) {
        int moved = 0;

        for (int i = 0; i < messageCount; i++) {
            Message message = rabbitTemplate.receive(dlqQueue);
            if (message == null) {
                log.info("No more messages in DLQ after requeuing {} message(s)", moved);
                break;
            }

            rabbitTemplate.send("", targetQueue, message);
            moved++;
        }

        return moved;
    }
}
