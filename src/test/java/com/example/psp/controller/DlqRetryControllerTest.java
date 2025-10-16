package com.example.psp.controller;

import com.example.psp.dto.DlqRetryRequest;
import com.example.psp.service.DlqRetryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DlqRetryController.class)
class DlqRetryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DlqRetryService dlqRetryService;

    @Test
    void retryMessagesReturnsOk() throws Exception {
        when(dlqRetryService.retryMessages(anyInt(), anyString())).thenReturn(2);

        mockMvc.perform(post("/api/admin/dlq/retry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"messageCount\":2,\"targetQueue\":\"webhook-queue\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void retryMessagesValidatesInput() throws Exception {
        mockMvc.perform(post("/api/admin/dlq/retry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"messageCount\":0,\"targetQueue\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
