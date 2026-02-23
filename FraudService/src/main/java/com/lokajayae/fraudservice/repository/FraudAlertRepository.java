package com.lokajayae.fraudservice.repository;

import com.lokajayae.fraudservice.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, String> {

    @Query(value = """
        SELECT * FROM fraud_alerts
        WHERE account_number = :accountNumber
        AND detected_at >= :since
        ORDER BY detected_at DESC
        """, nativeQuery = true)
    List<FraudAlert> findRecentAlertsByAccount(
            @Param("accountNumber") String accountNumber,
            @Param("since") LocalDateTime since);

    @Query(value = """
        SELECT * FROM fraud_alerts
        ORDER BY detected_at DESC
        """, nativeQuery = true)
    List<FraudAlert> findAllOrderByDetectedAtDesc();

    @Query(value = """
        SELECT * FROM fraud_alerts
        WHERE account_number = :accountNumber
        ORDER BY detected_at DESC
        """, nativeQuery = true)
    List<FraudAlert> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query(value = """
    SELECT * FROM fraud_alerts
    WHERE account_number = :accountNumber
    AND detected_at >= :since
    ORDER BY detected_at DESC
    LIMIT 1
    """, nativeQuery = true)
    FraudAlert findLatestAlertByAccount(
        @Param("accountNumber") String accountNumber,
        @Param("since") LocalDateTime since);
}