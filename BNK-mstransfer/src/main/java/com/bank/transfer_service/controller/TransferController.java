package com.bank.transfer_service.controller;

import com.bank.transfer_service.dto.TransferDTO;
import com.bank.transfer_service.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @GetMapping
    public ResponseEntity<List<TransferDTO>> getAllTransfers() {
        return ResponseEntity.ok(transferService.getAllTransfers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferDTO> getTransferById(@PathVariable Long id) {
        TransferDTO transfer = transferService.getTransferById(id);
        return transfer != null ? ResponseEntity.ok(transfer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TransferDTO> createTransfer(@RequestBody TransferDTO dto) {
        return ResponseEntity.ok(transferService.createTransfer(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<TransferDTO>> getTransfersByClientId(@PathVariable Long clientId) {
        List<TransferDTO> transfers = transferService.getTransfersByClientId(clientId);
        return ResponseEntity.ok(transfers);
    }

}