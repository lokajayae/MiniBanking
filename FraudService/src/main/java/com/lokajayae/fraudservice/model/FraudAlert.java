package com.lokajayae.fraudservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fraud_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String relatedTransactionIds;

    private LocalDateTime detectedAt;

    @PrePersist
    public void prePersist() {
        this.detectedAt = LocalDateTime.now();
    }
}