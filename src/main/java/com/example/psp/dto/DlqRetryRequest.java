package com.example.psp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DlqRetryRequest {

    @Min(value = 1, message = "messageCount must be at least 1")
    @Max(value = 100, message = "messageCount must not exceed 100")
    private int messageCount;

    @NotBlank(message = "targetQueue must not be blank")
    private String targetQueue;
}
