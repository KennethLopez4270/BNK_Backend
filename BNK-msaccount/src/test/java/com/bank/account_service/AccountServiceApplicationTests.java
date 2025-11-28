package com.bank.account_service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bank.account_service.controller.AccountController;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceApplicationTests {

	@Autowired
	private AccountController controller;


	// Prueba unitaria para verificar que el contexto de Spring se carga correctamente
	@Test
	void contextLoads() {

		assertThat(controller).isNotNull(); 
	}

	// Prueba unitaria para verificar que el controlador de cuentas se inyecta correctamente
	@Test
	void controllerAccountNoEsNull() {
		assert (controller != null);

	}

}
