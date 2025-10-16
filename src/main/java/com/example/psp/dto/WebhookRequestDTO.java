package com.example.psp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class WebhookRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "transactionId must not be blank")
    private String transactionId;

    @NotBlank(message = "paymentStatus must not be blank")
    private String paymentStatus;

    @NotNull(message = "amount must not be null")
    @Positive(message = "amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "currency must not be blank")
    private String currency;
    private String details; // Optional free-form field
}
