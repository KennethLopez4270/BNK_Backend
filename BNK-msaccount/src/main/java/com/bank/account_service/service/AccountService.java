package com.bank.account_service.service;
import com.bank.account_service.dto.AccountDTO;
import com.bank.account_service.entity.Account;
import com.bank.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public AccountDTO createAccount(AccountDTO dto) {
        Account account = mapToEntity(dto);
        return mapToDTO(accountRepository.save(account));
    }

    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        return accountRepository.findById(id).map(account -> {
            account.setAccountNumber(dto.getAccountNumber());
            account.setClientId(dto.getClientId());
            account.setAccountType(dto.getAccountType());
            account.setBalance(dto.getBalance());
            account.setStatus(dto.getStatus());
            return mapToDTO(accountRepository.save(account));
        }).orElse(null);
    }

    public boolean deleteAccount(String accountNumber) {
        // Validar que la cuenta existe antes de eliminar
        // Eliminar la cuenta
        try {
            String account = accountNumber;
            accountRepository.deleteByAccountNumber(accountNumber);
            if (account == null) {
                throw new RuntimeException("Cuenta no encontrada.");
            }
            return true;
        } catch (Exception e) {
            
            throw new RuntimeException("Error al eliminar la cuenta: " + e.getMessage());
            
        }
        //accountRepository.deleteByAccountNumber(accountNumber);
    }

    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setClientId(account.getClientId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        return dto;
    }

    private Account mapToEntity(AccountDTO dto) {
        Account account = new Account();
        account.setClientId(dto.getClientId());
        account.setAccountNumber(dto.getAccountNumber());
        account.setAccountType(dto.getAccountType());
        account.setBalance(dto.getBalance());
        account.setStatus(dto.getStatus());
        return account;
    }

    public AccountDTO deposit(Long id, BigDecimal amount) {
        return accountRepository.findById(id).map(account -> {
            account.setBalance(account.getBalance().add(amount));
            return mapToDTO(accountRepository.save(account));
        }).orElse(null);
    }

    public AccountDTO withdraw(Long id, BigDecimal amount) {
        return accountRepository.findById(id).map(account -> {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Fondos insuficientes.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            return mapToDTO(accountRepository.save(account));
        }).orElse(null);
    }

    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findByClientId(clientId);
        return accounts.stream().map(this::mapToDTO).collect(Collectors.toList());
    }


}