package com.bank.loan_service.service;


import com.bank.loan_service.dto.LoanDTO;
import com.bank.loan_service.entity.Loan;
import com.bank.loan_service.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public LoanDTO getLoanById(Long id) {
        return loanRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public LoanDTO createLoan(LoanDTO dto) {
        Loan loan = mapToEntity(dto);

        // calcular fecha fin y cuota mensual
        LocalDate today = LocalDate.now();
        loan.setStartDate(today);
        loan.setEndDate(today.plusMonths(loan.getTermMonths()));

        BigDecimal totalInterest = loan.getLoanAmount()
                .multiply(loan.getInterestRate())
                .divide(BigDecimal.valueOf(100));
        BigDecimal totalToPay = loan.getLoanAmount().add(totalInterest);
        BigDecimal monthlyPayment = totalToPay.divide(BigDecimal.valueOf(loan.getTermMonths()), 2, BigDecimal.ROUND_HALF_UP);

        loan.setMonthlyPayment(monthlyPayment);

        return mapToDTO(loanRepository.save(loan));
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    private LoanDTO mapToDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setClientId(loan.getClientId());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTermMonths(loan.getTermMonths());
        dto.setMonthlyPayment(loan.getMonthlyPayment());
        dto.setStartDate(loan.getStartDate());
        dto.setEndDate(loan.getEndDate());
        dto.setStatus(loan.getStatus());
        return dto;
    }

    private Loan mapToEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setClientId(dto.getClientId());
        loan.setLoanAmount(dto.getLoanAmount());
        loan.setInterestRate(dto.getInterestRate());
        loan.setTermMonths(dto.getTermMonths());
        loan.setStatus(dto.getStatus() != null ? dto.getStatus() : "activo");
        return loan;
    }
}
