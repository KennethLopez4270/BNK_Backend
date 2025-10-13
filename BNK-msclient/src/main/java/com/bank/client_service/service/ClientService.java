package com.bank.client_service.service;
import com.bank.client_service.dto.ClientDTO;
import com.bank.client_service.entity.Client;
import com.bank.client_service.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ClientDTO getClientById(Long id) {
        return clientRepository.findById(id).map(this::mapToDTO).orElse(null);
    }

    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = mapToEntity(clientDTO);
        return mapToDTO(clientRepository.save(client));
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        return clientRepository.findById(id).map(client -> {
            client.setFullName(clientDTO.getFullName());
            client.setEmail(clientDTO.getEmail());
            client.setPhoneNumber(clientDTO.getPhoneNumber());
            client.setAddress(clientDTO.getAddress());
            client.setDocumentNumber(clientDTO.getDocumentNumber());
            client.setDocumentType(clientDTO.getDocumentType());
            return mapToDTO(clientRepository.save(client));
        }).orElse(null);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    private ClientDTO mapToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setFullName(client.getFullName());
        dto.setEmail(client.getEmail());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setAddress(client.getAddress());
        dto.setDocumentNumber(client.getDocumentNumber());
        dto.setDocumentType(client.getDocumentType());
        return dto;
    }

    private Client mapToEntity(ClientDTO dto) {
        Client client = new Client();
        client.setFullName(dto.getFullName());
        client.setEmail(dto.getEmail());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setAddress(dto.getAddress());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setDocumentType(dto.getDocumentType());
        return client;
    }
}

