package com.example.psp.controller;

import com.example.psp.dto.WebhookRequestDTO;
import com.example.psp.service.IdempotencyService;
import com.example.psp.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final WebhookService webhookService;
    private final IdempotencyService idempotencyService;

    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody @Valid WebhookRequestDTO request) {
        if (request.getTransactionId() != null) {
            MDC.put("transactionId", request.getTransactionId());
        }

        log.info("Received webhook request: {}", request.getTransactionId());

        if (idempotencyService.isRequestProcessed(request.getTransactionId())) {
            log.info("Duplicate webhook request received, ignoring: {}", request.getTransactionId());
            return ResponseEntity.ok("Request already processed.");
        }

        try {
            webhookService.processWebhook(request);
            idempotencyService.markRequestAsProcessed(request.getTransactionId());
            log.info("Webhook request accepted and queued for processing: {}", request.getTransactionId());
            return ResponseEntity.accepted().body("Webhook accepted for processing.");
        } catch (Exception e) {
            log.error("Error processing webhook: {}", request.getTransactionId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook.");
        } finally {
            MDC.remove("transactionId");
        }
    }
}
