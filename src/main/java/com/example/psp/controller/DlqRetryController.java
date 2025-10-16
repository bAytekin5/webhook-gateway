package com.example.psp.controller;

import com.example.psp.dto.DlqRetryRequest;
import com.example.psp.service.DlqRetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dlq")
@RequiredArgsConstructor
@Slf4j
public class DlqRetryController {

    private final DlqRetryService dlqRetryService;

    /**
     * Requeue messages from the configured DLQ into the provided target queue.
     */
    @PostMapping("/retry")
    public ResponseEntity<String> retryMessages(@RequestBody @Valid DlqRetryRequest request) {
        int retried = dlqRetryService.retryMessages(request.getMessageCount(), request.getTargetQueue());
        log.info("Requeued {} messages from DLQ to {}", retried, request.getTargetQueue());
        return ResponseEntity.ok("Requeued " + retried + " message(s).");
    }
}
