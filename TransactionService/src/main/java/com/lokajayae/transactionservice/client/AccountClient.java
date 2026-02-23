package com.lokajayae.transactionservice.client;

import com.lokajayae.transactionservice.dto.response.AccountResponse;
import com.lokajayae.transactionservice.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AccountClient {

    private final RestTemplate restTemplate;

    @Value("${account.service.url}")
    private String accountServiceUrl;

    public AccountResponse getAccount(String accountNumber) {
        try {
            ApiResponse<AccountResponse> response = restTemplate.exchange(
                    accountServiceUrl + "/api/accounts/" + accountNumber,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<AccountResponse>>() {}
            ).getBody();

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Account not found");
            }

            return response.getData();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Account not found");
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Account service error: " + e.getStatusCode());
        }
    }

    public void updateBalance(String accountNumber, BigDecimal newBalance) {
        try {
            restTemplate.put(
                    accountServiceUrl + "/api/accounts/" + accountNumber + "/balance",
                    newBalance
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Account not found");
        }
    }
}