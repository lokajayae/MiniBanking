package com.lokajayae.accountservice.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountResponse {
    private String id;
    private String name;
    private String accountNumber;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}