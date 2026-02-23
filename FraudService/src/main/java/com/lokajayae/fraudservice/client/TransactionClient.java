package com.lokajayae.fraudservice.client;

import com.lokajayae.fraudservice.dto.response.ApiResponse;
import com.lokajayae.fraudservice.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class TransactionClient {

    private final RestTemplate restTemplate;

    @Value("${transaction.service.url}")
    private String transactionServiceUrl;

    public TransactionResponse getTransaction(String transactionId) {
        try {
            ApiResponse<TransactionResponse> response = restTemplate.exchange(
                    transactionServiceUrl + "/api/transactions/" + transactionId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<TransactionResponse>>() {}
            ).getBody();

            if (response == null || response.getData() == null) {
                return null;
            }

            return response.getData();
        } catch (HttpClientErrorException e) {
            return null;
        }
    }
}