package com.rebwon.demojwt.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rebwon.demojwt.exception.ResourceNotFoundException;
import com.rebwon.demojwt.security.UserAccount;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Account account = accountRepository.findByEmail(email).orElseThrow(
			() -> new UsernameNotFoundException("User not found with email:" + email));
		return UserAccount.create(account);
	}

	@Override
	public UserDetails loadUserById(Long id) {
		Account account = accountRepository.findById(id).orElseThrow(
			() -> new ResourceNotFoundException("User", "id", id)
		);
		return UserAccount.create(account);
	}
}
