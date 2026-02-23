package com.lokajayae.accountservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal initialBalance;
}