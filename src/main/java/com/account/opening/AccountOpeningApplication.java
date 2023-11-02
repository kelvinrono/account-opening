package com.account.opening;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AccountOpeningApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountOpeningApplication.class, args);
	}

}
