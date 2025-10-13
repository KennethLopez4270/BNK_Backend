package com.bank.transfer_service.service;
import com.bank.transfer_service.dto.TransferDTO;
import com.bank.transfer_service.entity.Transfer;
import com.bank.transfer_service.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    public List<TransferDTO> getAllTransfers() {
        return transferRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public TransferDTO getTransferById(Long id) {
        return transferRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public TransferDTO createTransfer(TransferDTO dto) {
        Transfer transfer = mapToEntity(dto);
        return mapToDTO(transferRepository.save(transfer));
    }

    public void deleteTransfer(Long id) {
        transferRepository.deleteById(id);
    }

    private TransferDTO mapToDTO(Transfer transfer) {
        TransferDTO dto = new TransferDTO();
        dto.setId(transfer.getId());
        dto.setOriginAccountId(transfer.getOriginAccountId());
        dto.setDestinationAccountNumber(transfer.getDestinationAccountNumber());
        dto.setDestinationBank(transfer.getDestinationBank());
        dto.setAmount(transfer.getAmount());
        dto.setStatus(transfer.getStatus());
        return dto;
    }

    private Transfer mapToEntity(TransferDTO dto) {
        Transfer transfer = new Transfer();
        transfer.setOriginAccountId(dto.getOriginAccountId());
        transfer.setDestinationAccountNumber(dto.getDestinationAccountNumber());
        transfer.setDestinationBank(dto.getDestinationBank());
        transfer.setAmount(dto.getAmount());
        transfer.setStatus(dto.getStatus() != null ? dto.getStatus() : "completado");
        return transfer;
    }

    public List<TransferDTO> getTransfersByClientId(Long clientId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/api/accounts/client/" + clientId;

        List<Map<String, Object>> accountList;
        try {
            accountList = restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            System.out.println("Error al obtener cuentas del cliente: " + e.getMessage());
            return Collections.emptyList();
        }

        if (accountList == null || accountList.isEmpty()) {
            System.out.println("No se encontraron cuentas para el cliente con ID " + clientId);
            return Collections.emptyList();
        }

        List<Long> accountIds = accountList.stream()
                .map(acc -> ((Number) acc.get("id")).longValue())
                .collect(Collectors.toList());

        return transferRepository.findAll().stream()
                .filter(transfer -> accountIds.contains(transfer.getOriginAccountId()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
