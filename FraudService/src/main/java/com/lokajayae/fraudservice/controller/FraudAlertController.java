package com.lokajayae.fraudservice.controller;

import com.lokajayae.fraudservice.dto.response.ApiResponse;
import com.lokajayae.fraudservice.dto.response.FraudAlertResponse;
import com.lokajayae.fraudservice.service.FraudAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success(fraudAlertService.getAllAlerts(), "Success Get Alerts"));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<List<FraudAlertResponse>>> getAlertsByAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(
                fraudAlertService.getAlertsByAccount(accountNumber), "Success Get Alerts"));
    }
}