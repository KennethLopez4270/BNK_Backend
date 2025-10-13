package com.bank.payment_service.service;
import com.bank.payment_service.dto.PaymentDTO;
import com.bank.payment_service.entity.Payment;
import com.bank.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = mapToEntity(dto);
        return mapToDTO(paymentRepository.save(payment));
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    private PaymentDTO mapToDTO(Payment entity) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(entity.getId());
        dto.setAccountId(entity.getAccountId());
        dto.setServiceType(entity.getServiceType());
        dto.setServiceReference(entity.getServiceReference());
        dto.setAmount(entity.getAmount());
        return dto;
    }

    private Payment mapToEntity(PaymentDTO dto) {
        Payment entity = new Payment();
        entity.setAccountId(dto.getAccountId());
        entity.setServiceType(dto.getServiceType());
        entity.setServiceReference(dto.getServiceReference());
        entity.setAmount(dto.getAmount());
        return entity;
    }
}

