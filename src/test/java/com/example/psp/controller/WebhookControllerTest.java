package com.example.psp.controller;

import com.example.psp.dto.WebhookRequestDTO;
import com.example.psp.service.IdempotencyService;
import com.example.psp.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebhookService webhookService;

    @MockBean
    private IdempotencyService idempotencyService;

    @Test
    void returnsBadRequestWhenTransactionIdMissing() throws Exception {
        String payload = "{\"paymentStatus\":\"SUCCESS\",\"amount\":50.0,\"currency\":\"USD\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.transactionId").value("transactionId must not be blank"));
    }

    @Test
    void returnsOkForDuplicateRequest() throws Exception {
        when(idempotencyService.isRequestProcessed("txn-dup"))
                .thenReturn(true);

        String payload = "{\"transactionId\":\"txn-dup\",\"paymentStatus\":\"SUCCESS\",\"amount\":100.0,\"currency\":\"USD\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Request already processed."));

        verify(idempotencyService, times(1)).isRequestProcessed("txn-dup");
        verify(webhookService, times(0)).processWebhook(Mockito.any(WebhookRequestDTO.class));
    }

    @Test
    void returnsAcceptedWhenProcessingSuccessful() throws Exception {
        when(idempotencyService.isRequestProcessed("txn-accepted"))
                .thenReturn(false);

        String payload = "{\"transactionId\":\"txn-accepted\",\"paymentStatus\":\"SUCCESS\",\"amount\":150.0,\"currency\":\"EUR\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Webhook accepted for processing."));

        verify(webhookService, times(1)).processWebhook(Mockito.any(WebhookRequestDTO.class));
        verify(idempotencyService, times(1)).markRequestAsProcessed("txn-accepted");
    }

    @Test
    void returnsInternalServerErrorWhenProcessingFails() throws Exception {
        when(idempotencyService.isRequestProcessed("txn-error"))
                .thenReturn(false);

        doThrow(new RuntimeException("Queue failure"))
                .when(webhookService).processWebhook(Mockito.any(WebhookRequestDTO.class));

        String payload = "{\"transactionId\":\"txn-error\",\"paymentStatus\":\"SUCCESS\",\"amount\":75.0,\"currency\":\"GBP\"}";

        mockMvc.perform(post("/api/webhooks/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error processing webhook."));
    }
}

