package com.rebwon.demojwt.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rebwon.demojwt.domain.Account;
import com.rebwon.demojwt.domain.AccountRepository;
import com.rebwon.demojwt.domain.CurrentUser;
import com.rebwon.demojwt.exception.ResourceNotFoundException;
import com.rebwon.demojwt.security.UserAccount;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	private final AccountRepository accountRepository;

	@GetMapping("/user/me")
	@PreAuthorize("hasRole('USER')")
	public Account getCurrentAccount(@CurrentUser UserAccount account) {
		return accountRepository.findById(account.getId())
			.orElseThrow(() -> new ResourceNotFoundException("User", "id", account.getId()));
	}
}
