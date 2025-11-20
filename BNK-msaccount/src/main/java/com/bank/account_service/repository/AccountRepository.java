package com.bank.account_service.repository;
//import com.bank.account_service.dto.AccountDTO;
import com.bank.account_service.entity.Account;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByClientId(Long clientId);

    // Opción 1: Query personalizada
    // @Modifying
    // @Query("DELETE FROM accounts a WHERE a.account_number = :accountNumber")
    // void deleteByAccountNumber(@Param("accountNumber") String accountNumber);

    // ✅ CORRECTO: Sin guión bajo
    Optional<Account> findByAccountNumber(String accountNumber);
    

    @Transactional
    @Modifying
    void deleteByAccountNumber(String accountNumber);

}

