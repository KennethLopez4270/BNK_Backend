package com.bank.account_service;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bank.account_service.controller.AccountController;

@SpringBootTest
class AccountServiceApplicationTests {

	@Autowired
	private AccountController controller;

	@Test
	void contextLoads() {

		assert (controller != null);
	}

	@Test
	void controllerAccountNoEsNull() {
		assert (controller != null);

	}

}
