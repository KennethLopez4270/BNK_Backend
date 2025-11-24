package com.bank.loan_service;

import com.bank.loan_service.dto.LoanDTO;
import com.bank.loan_service.entity.Loan;
import com.bank.loan_service.repository.LoanRepository;
import com.bank.loan_service.service.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoanServiceApplicationTests {

	@Mock
	private LoanRepository loanRepository;

	@InjectMocks
	private LoanService loanService;

	// 1. Test Create Loan - Success
	// Verifica que al crear un préstamo se calculen correctamente las fechas y
	// pagos, y se guarde.
	@Test
	void testCreateLoan_Success() {
		LoanDTO inputDto = new LoanDTO();
		inputDto.setClientId(1L);
		inputDto.setLoanAmount(new BigDecimal("1000"));
		inputDto.setInterestRate(new BigDecimal("10")); // 10%
		inputDto.setTermMonths(12);

		Loan savedLoan = new Loan();
		savedLoan.setId(1L);
		savedLoan.setClientId(1L);
		savedLoan.setLoanAmount(new BigDecimal("1000"));
		savedLoan.setInterestRate(new BigDecimal("10"));
		savedLoan.setTermMonths(12);
		savedLoan.setStartDate(LocalDate.now());
		savedLoan.setEndDate(LocalDate.now().plusMonths(12));
		// Interes = 1000 * 10 / 100 = 100. Total = 1100. Monthly = 1100 / 12 = 91.67
		savedLoan.setMonthlyPayment(new BigDecimal("91.67"));
		savedLoan.setStatus("activo");

		when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

		LoanDTO result = loanService.createLoan(inputDto);

		assertNotNull(result);
		assertEquals(new BigDecimal("91.67"), result.getMonthlyPayment());
		assertEquals("activo", result.getStatus());
		verify(loanRepository, times(1)).save(any(Loan.class));
	}

	// 2. Test Get Loan By ID - Found
	// Verifica que se pueda recuperar un préstamo existente por su ID.
	@Test
	void testGetLoanById_Found() {
		Loan loan = new Loan();
		loan.setId(1L);
		loan.setClientId(1L);
		when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

		LoanDTO result = loanService.getLoanById(1L);

		assertNotNull(result);
		assertEquals(1L, result.getId());
	}

	// 3. Test Get Loan By ID - Not Found
	// Verifica que devuelva null si el préstamo no existe.
	@Test
	void testGetLoanById_NotFound() {
		when(loanRepository.findById(99L)).thenReturn(Optional.empty());

		LoanDTO result = loanService.getLoanById(99L);

		assertNull(result);
	}

	// 4. Test Get All Loans
	// Verifica que se listen todos los préstamos.
	@Test
	void testGetAllLoans() {
		Loan loan1 = new Loan();
		Loan loan2 = new Loan();
		when(loanRepository.findAll()).thenReturn(Arrays.asList(loan1, loan2));

		List<LoanDTO> result = loanService.getAllLoans();

		assertEquals(2, result.size());
	}

	// 5. Test Delete Loan
	// Verifica que se llame al repositorio para eliminar un préstamo.
	@Test
	void testDeleteLoan() {
		doNothing().when(loanRepository).deleteById(1L);

		loanService.deleteLoan(1L);

		verify(loanRepository, times(1)).deleteById(1L);
	}

	// 6. Test Calculate Monthly Payment Logic
	// Verifica específicamente el cálculo de la cuota mensual.
	@Test
	void testCalculateMonthlyPaymentLogic() {
		LoanDTO inputDto = new LoanDTO();
		inputDto.setLoanAmount(new BigDecimal("5000"));
		inputDto.setInterestRate(new BigDecimal("5")); // 5%
		inputDto.setTermMonths(24);

		// Expected:
		// Interest = 5000 * 0.05 = 250
		// Total = 5250
		// Monthly = 5250 / 24 = 218.75

		Loan savedLoan = new Loan();
		savedLoan.setMonthlyPayment(new BigDecimal("218.75"));
		when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

		LoanDTO result = loanService.createLoan(inputDto);

		assertEquals(new BigDecimal("218.75"), result.getMonthlyPayment());
	}

	// 7. Test Default Status
	// Verifica que el estado por defecto sea "activo" si no se envía.
	@Test
	void testDefaultStatus() {
		LoanDTO inputDto = new LoanDTO();
		inputDto.setLoanAmount(BigDecimal.TEN);
		inputDto.setInterestRate(BigDecimal.ONE);
		inputDto.setTermMonths(1);

		Loan savedLoan = new Loan();
		savedLoan.setStatus("activo");
		when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

		LoanDTO result = loanService.createLoan(inputDto);

		assertEquals("activo", result.getStatus());
	}

	// 8. Test Custom Status
	// Verifica que se respete el estado enviado en el DTO.
	@Test
	void testCustomStatus() {
		LoanDTO inputDto = new LoanDTO();
		inputDto.setLoanAmount(BigDecimal.TEN);
		inputDto.setInterestRate(BigDecimal.ONE);
		inputDto.setTermMonths(1);
		inputDto.setStatus("pendiente");

		Loan savedLoan = new Loan();
		savedLoan.setStatus("pendiente");
		when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

		LoanDTO result = loanService.createLoan(inputDto);

		assertEquals("pendiente", result.getStatus());
	}

	// 9. [FAILING] Test Wrong Interest Calculation Expectation
	// Este test está DISEÑADO PARA FALLAR. Espera un cálculo incorrecto
	// intencionalmente.
	@Test
	void testCreateLoan_InterestCalculationError_FAILING() {
		LoanDTO inputDto = new LoanDTO();
		inputDto.setLoanAmount(new BigDecimal("1000"));
		inputDto.setInterestRate(new BigDecimal("10"));
		inputDto.setTermMonths(12);

		Loan savedLoan = new Loan();
		// El servicio calcula 91.67, pero aquí simulamos que el repo devuelve eso
		savedLoan.setMonthlyPayment(new BigDecimal("91.67"));
		when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);

		LoanDTO result = loanService.createLoan(inputDto);

		// FALLARÁ AQUÍ: Esperamos 9999 pero el resultado real será 91.67
		assertEquals(new BigDecimal("9999.00"), result.getMonthlyPayment(),
				"Este test debe fallar porque el cálculo esperado es incorrecto");
	}

	// 10. [FAILING] Test Get Loan Returns Wrong ID
	// Este test está DISEÑADO PARA FALLAR. Verifica que el ID devuelto sea
	// diferente al solicitado.
	@Test
	void testGetLoanById_WrongId_FAILING() {
		Loan loan = new Loan();
		loan.setId(1L);
		when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

		LoanDTO result = loanService.getLoanById(1L);

		// FALLARÁ AQUÍ: El ID es 1, pero asertamos que sea 2
		assertEquals(2L, result.getId(), "Este test debe fallar porque el ID devuelto es 1, no 2");
	}

}
