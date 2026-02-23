package com.lokajayae.accountservice.repository;

import com.lokajayae.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query(value = """
        SELECT * FROM accounts
        WHERE balance >= :minBalance
        ORDER BY balance DESC
        """, nativeQuery = true)
    List<Account> findAccountsWithMinBalance(@Param("minBalance") BigDecimal minBalance);
}