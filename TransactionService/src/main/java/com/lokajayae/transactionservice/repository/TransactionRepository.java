package com.lokajayae.transactionservice.repository;

import com.lokajayae.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query(value = """
        SELECT * FROM transactions
        WHERE from_account_number = :accountNumber
        OR to_account_number = :accountNumber
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<Transaction> findTransactionHistory(@Param("accountNumber") String accountNumber);

    @Query(value = """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE from_account_number = :accountNumber
        AND status = 'SUCCESS'
        """, nativeQuery = true)
    BigDecimal getTotalAmountSent(@Param("accountNumber") String accountNumber);

    @Query(value = """
        SELECT * FROM transactions
        WHERE amount >= :minAmount
        AND status = 'SUCCESS'
        ORDER BY amount DESC
        """, nativeQuery = true)
    List<Transaction> findLargeTransactions(@Param("minAmount") BigDecimal minAmount);
}