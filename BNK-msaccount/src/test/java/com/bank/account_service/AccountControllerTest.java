package com.bank.account_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bank.account_service.controller.AccountController;
import com.bank.account_service.dto.AccountDTO;
import com.bank.account_service.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests para AccountController
 * Usa @WebMvcTest para probar solo la capa web (Controller)
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;
    

    @Test
    void getAllAccounts_DeberiaRetornarListaDeCuentas() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuenta1 = new AccountDTO();
        cuenta1.setId(1L);
        cuenta1.setClientId(100L);
        cuenta1.setAccountNumber("1234567890");
        cuenta1.setAccountType("AHORROS");
        cuenta1.setBalance(BigDecimal.valueOf(1000.00));
        cuenta1.setStatus("ACTIVA");

        AccountDTO cuenta2 = new AccountDTO();
        cuenta2.setId(2L);
        cuenta2.setClientId(101L);
        cuenta2.setAccountNumber("0987654321");
        cuenta2.setAccountType("CORRIENTE");
        cuenta2.setBalance(BigDecimal.valueOf(2500.00));
        cuenta2.setStatus("ACTIVA");

        List<AccountDTO> cuentasEsperadas = Arrays.asList(cuenta1, cuenta2);

        // Configuramos el SERVICIO (no el controller) para retornar datos de prueba
        when(accountService.getAllAccounts()).thenReturn(cuentasEsperadas);

        // ========== EJECUCIÓN (Act) + VERIFICACIÓN (Assert) ==========
        mockMvc.perform(get("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].accountNumber", is("1234567890")))
                .andExpect(jsonPath("$[0].balance", is(1000.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].accountNumber", is("0987654321")))
                .andExpect(jsonPath("$[1].balance", is(2500.0)));
    }

    @Test
    void getAllAccounts_CuandoNoHayCuentas_DeberiaRetornarListaVacia() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        // ========== EJECUCIÓN (Act) + VERIFICACIÓN (Assert) ==========
        mockMvc.perform(get("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAccountById_CuandoExiste_DeberiaRetornarCuenta() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuenta = new AccountDTO();
        cuenta.setId(1L);
        cuenta.setClientId(100L);
        cuenta.setAccountNumber("1234567890");
        cuenta.setAccountType("AHORROS");
        cuenta.setBalance(BigDecimal.valueOf(1000.00));
        cuenta.setStatus("ACTIVA");

        when(accountService.getAccountById(1L)).thenReturn(cuenta);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(get("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountNumber", is("1234567890")));
    }

    @Test
    void getAccountById_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        when(accountService.getAccountById(999L)).thenReturn(null);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(get("/api/accounts/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAccountsByClientId_DeberiaRetornarCuentasDelCliente() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuenta1 = new AccountDTO();
        cuenta1.setId(1L);
        cuenta1.setClientId(100L);
        cuenta1.setAccountNumber("1234567890");
        cuenta1.setAccountType("AHORROS");
        cuenta1.setBalance(BigDecimal.valueOf(1000.00));
        cuenta1.setStatus("ACTIVA");

        AccountDTO cuenta2 = new AccountDTO();
        cuenta2.setId(2L);
        cuenta2.setClientId(100L);
        cuenta2.setAccountNumber("1111111111");
        cuenta2.setAccountType("CORRIENTE");
        cuenta2.setBalance(BigDecimal.valueOf(500.00));
        cuenta2.setStatus("ACTIVA");

        List<AccountDTO> cuentasCliente = Arrays.asList(cuenta1, cuenta2);
        when(accountService.getAccountsByClientId(100L)).thenReturn(cuentasCliente);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(get("/api/accounts/client/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].clientId", is(100)))
                .andExpect(jsonPath("$[1].clientId", is(100)));
    }

    // test POST (Crear Cuentas nuevas)
    
    @Test
    void createAccount_ConDatosValidos_DeberiaCrearCuenta() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO nuevaCuenta = new AccountDTO();
        nuevaCuenta.setClientId(100L);
        nuevaCuenta.setAccountNumber("5555555555");
        nuevaCuenta.setAccountType("AHORROS");
        nuevaCuenta.setBalance(BigDecimal.valueOf(500.00));
        nuevaCuenta.setStatus("ACTIVA");

        AccountDTO cuentaCreada = new AccountDTO();
        cuentaCreada.setId(3L);
        cuentaCreada.setClientId(100L);
        cuentaCreada.setAccountNumber("5555555555");
        cuentaCreada.setAccountType("AHORROS");
        cuentaCreada.setBalance(BigDecimal.valueOf(500.00));
        cuentaCreada.setStatus("ACTIVA");

        when(accountService.createAccount(any(AccountDTO.class))).thenReturn(cuentaCreada);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevaCuenta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.accountNumber", is("5555555555")))
                .andExpect(jsonPath("$.balance", is(500.0)));
    }

    // TESTS PARA PUT (Actualizar Cuenta)
    
    @Test
    void updateAccount_CuandoExiste_DeberiaActualizarCuenta() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuentaActualizada = new AccountDTO();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setClientId(100L);
        cuentaActualizada.setAccountNumber("1234567890");
        cuentaActualizada.setAccountType("CORRIENTE");
        cuentaActualizada.setBalance(BigDecimal.valueOf(1500.00));
        cuentaActualizada.setStatus("ACTIVA");

        when(accountService.updateAccount(eq(1L), any(AccountDTO.class))).thenReturn(cuentaActualizada);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountType", is("CORRIENTE")))
                .andExpect(jsonPath("$.balance", is(1500.0)));
    }

    @Test
    void updateAccount_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuentaActualizada = new AccountDTO();
        cuentaActualizada.setAccountType("CORRIENTE");

        when(accountService.updateAccount(eq(999L), any(AccountDTO.class))).thenReturn(null);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(put("/api/accounts/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuentaActualizada)))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTS PARA DELETE (Eliminar Cuenta) ====================
    
    @Test
    void deleteAccount_CuandoExiste_DeberiaEliminarCuenta() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        // No lanza excepción = eliminación exitosa
        
        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(delete("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // @Test
    // void deleteAccount_CuandoNoExiste_DeberiaRetornar404() throws Exception {
    //     // ========== PREPARACIÓN (Arrange) ==========
    //     // Configuramos el servicio para lanzar RuntimeException cuando no encuentra la cuenta
    //     when(accountService.deleteAccount("999")).thenThrow(new RuntimeException("Cuenta no encontrada"));

    //     // ========== EJECUCIÓN + VERIFICACIÓN ==========
    //     mockMvc.perform(delete("/api/accounts/999")
    //                     .contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(status().isNotFound());
    // }

    // ==================== TESTS PARA PATCH /deposit (Depósito) ====================
    
    @Test
    void deposit_ConMontoValido_DeberiaAumentarBalance() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuentaActualizada = new AccountDTO();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setClientId(100L);
        cuentaActualizada.setAccountNumber("1234567890");
        cuentaActualizada.setAccountType("AHORROS");
        cuentaActualizada.setBalance(BigDecimal.valueOf(1500.00));
        cuentaActualizada.setStatus("ACTIVA");

        when(accountService.deposit(eq(1L), eq(BigDecimal.valueOf(500.00)))).thenReturn(cuentaActualizada);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(patch("/api/accounts/1/deposit")
                        .param("amount", "500.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(1500.0)));
    }

    @Test
    void deposit_CuandoCuentaNoExiste_DeberiaRetornar404() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        when(accountService.deposit(eq(999L), any(BigDecimal.class))).thenReturn(null);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(patch("/api/accounts/999/deposit")
                        .param("amount", "500.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ==================== TESTS PARA PATCH /withdraw (Retiro) ====================
    
    @Test
    void withdraw_ConMontoValido_DeberiaDisminuirBalance() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        AccountDTO cuentaActualizada = new AccountDTO();
        cuentaActualizada.setId(1L);
        cuentaActualizada.setClientId(100L);
        cuentaActualizada.setAccountNumber("1234567890");
        cuentaActualizada.setAccountType("AHORROS");
        cuentaActualizada.setBalance(BigDecimal.valueOf(500.00));
        cuentaActualizada.setStatus("ACTIVA");

        when(accountService.withdraw(eq(1L), eq(BigDecimal.valueOf(500.00)))).thenReturn(cuentaActualizada);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(patch("/api/accounts/1/withdraw")
                        .param("amount", "500.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.balance", is(500.0)));
    }

    @Test
    void withdraw_ConSaldoInsuficiente_DeberiaRetornar400() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        when(accountService.withdraw(eq(1L), eq(BigDecimal.valueOf(5000.00))))
                .thenThrow(new IllegalArgumentException("Saldo insuficiente"));

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(patch("/api/accounts/1/withdraw")
                        .param("amount", "5000.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void withdraw_CuandoCuentaNoExiste_DeberiaRetornar404() throws Exception {
        // ========== PREPARACIÓN (Arrange) ==========
        when(accountService.withdraw(eq(999L), any(BigDecimal.class))).thenReturn(null);

        // ========== EJECUCIÓN + VERIFICACIÓN ==========
        mockMvc.perform(patch("/api/accounts/999/withdraw")
                        .param("amount", "100.00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}