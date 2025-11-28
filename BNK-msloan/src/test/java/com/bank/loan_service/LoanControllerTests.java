package com.bank.loan_service;

import com.bank.loan_service.controller.LoanController;
import com.bank.loan_service.dto.LoanDTO;
import com.bank.loan_service.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. Test GET /api/loans - Listar todos los préstamos
    // Verifica que el endpoint devuelva una lista de préstamos con status 200
    @Test
    void testGetAllLoans_ReturnsOk() throws Exception {
        LoanDTO loan1 = new LoanDTO();
        loan1.setId(1L);
        loan1.setClientId(100L);
        loan1.setLoanAmount(new BigDecimal("1000"));
        loan1.setStatus("activo");

        LoanDTO loan2 = new LoanDTO();
        loan2.setId(2L);
        loan2.setClientId(200L);
        loan2.setLoanAmount(new BigDecimal("2000"));
        loan2.setStatus("activo");

        List<LoanDTO> loans = Arrays.asList(loan1, loan2);
        when(loanService.getAllLoans()).thenReturn(loans);

        mockMvc.perform(get("/api/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].clientId", is(100)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].clientId", is(200)));

        verify(loanService, times(1)).getAllLoans();
    }

    // 2. Test GET /api/loans/{id} - Obtener préstamo por ID (existente)
    // Verifica que el endpoint devuelva el préstamo con status 200
    @Test
    void testGetLoanById_Found_ReturnsOk() throws Exception {
        LoanDTO loan = new LoanDTO();
        loan.setId(1L);
        loan.setClientId(100L);
        loan.setLoanAmount(new BigDecimal("5000"));
        loan.setInterestRate(new BigDecimal("10"));
        loan.setTermMonths(12);
        loan.setMonthlyPayment(new BigDecimal("458.33"));
        loan.setStatus("activo");

        when(loanService.getLoanById(1L)).thenReturn(loan);

        mockMvc.perform(get("/api/loans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.clientId", is(100)))
                .andExpect(jsonPath("$.loanAmount", is(5000)))
                .andExpect(jsonPath("$.status", is("activo")));

        verify(loanService, times(1)).getLoanById(1L);
    }

    // 3. Test GET /api/loans/{id} - Préstamo no encontrado
    // Verifica que el endpoint devuelva status 404 cuando el préstamo no existe
    @Test
    void testGetLoanById_NotFound_Returns404() throws Exception {
        when(loanService.getLoanById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/loans/99"))
                .andExpect(status().isNotFound());

        verify(loanService, times(1)).getLoanById(99L);
    }

    // 4. Test POST /api/loans - Crear un nuevo préstamo
    // Verifica que el endpoint cree un préstamo y devuelva status 200 con el DTO
    @Test
    void testCreateLoan_ReturnsCreatedLoan() throws Exception {
        LoanDTO inputDto = new LoanDTO();
        inputDto.setClientId(100L);
        inputDto.setLoanAmount(new BigDecimal("3000"));
        inputDto.setInterestRate(new BigDecimal("8"));
        inputDto.setTermMonths(24);

        LoanDTO savedDto = new LoanDTO();
        savedDto.setId(1L);
        savedDto.setClientId(100L);
        savedDto.setLoanAmount(new BigDecimal("3000"));
        savedDto.setInterestRate(new BigDecimal("8"));
        savedDto.setTermMonths(24);
        savedDto.setMonthlyPayment(new BigDecimal("135.00"));
        savedDto.setStartDate(LocalDate.now());
        savedDto.setEndDate(LocalDate.now().plusMonths(24));
        savedDto.setStatus("activo");

        when(loanService.createLoan(any(LoanDTO.class))).thenReturn(savedDto);

        String requestBody = objectMapper.writeValueAsString(inputDto);

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.clientId", is(100)))
                .andExpect(jsonPath("$.loanAmount", is(3000)))
                .andExpect(jsonPath("$.monthlyPayment", is(135.00)))
                .andExpect(jsonPath("$.status", is("activo")));

        verify(loanService, times(1)).createLoan(any(LoanDTO.class));
    }

    // 5. Test DELETE /api/loans/{id} - Eliminar un préstamo
    // Verifica que el endpoint elimine el préstamo y devuelva status 204
    @Test
    void testDeleteLoan_ReturnsNoContent() throws Exception {
        doNothing().when(loanService).deleteLoan(1L);

        mockMvc.perform(delete("/api/loans/1"))
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).deleteLoan(1L);
    }

    // 6. Test POST /api/loans - Validar campos requeridos del JSON
    // Verifica que el endpoint procese correctamente todos los campos del DTO
    @Test
    void testCreateLoan_WithAllFields() throws Exception {
        LoanDTO inputDto = new LoanDTO();
        inputDto.setClientId(200L);
        inputDto.setLoanAmount(new BigDecimal("10000"));
        inputDto.setInterestRate(new BigDecimal("12"));
        inputDto.setTermMonths(36);
        inputDto.setStatus("pendiente");

        LoanDTO savedDto = new LoanDTO();
        savedDto.setId(2L);
        savedDto.setClientId(200L);
        savedDto.setLoanAmount(new BigDecimal("10000"));
        savedDto.setInterestRate(new BigDecimal("12"));
        savedDto.setTermMonths(36);
        savedDto.setMonthlyPayment(new BigDecimal("366.67"));
        savedDto.setStartDate(LocalDate.now());
        savedDto.setEndDate(LocalDate.now().plusMonths(36));
        savedDto.setStatus("pendiente");

        when(loanService.createLoan(any(LoanDTO.class))).thenReturn(savedDto);

        String requestBody = objectMapper.writeValueAsString(inputDto);

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("pendiente")))
                .andExpect(jsonPath("$.termMonths", is(36)));

        verify(loanService, times(1)).createLoan(any(LoanDTO.class));
    }
}
