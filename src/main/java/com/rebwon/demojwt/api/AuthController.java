package com.rebwon.demojwt.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rebwon.demojwt.domain.Account;
import com.rebwon.demojwt.domain.AccountRepository;
import com.rebwon.demojwt.domain.AuthProvider;
import com.rebwon.demojwt.exception.BadRequestException;
import com.rebwon.demojwt.payload.ApiResponse;
import com.rebwon.demojwt.payload.AuthResponse;
import com.rebwon.demojwt.payload.LoginRequest;
import com.rebwon.demojwt.payload.SignUpRequest;
import com.rebwon.demojwt.security.TokenProvider;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.getEmail(),
				loginRequest.getPassword()
			)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = tokenProvider.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		if(accountRepository.existsByEmail(signUpRequest.getEmail())) {
			throw new BadRequestException("Email address already in use.");
		}

		Account account = new Account();
		account.setName(signUpRequest.getName());
		account.setEmail(signUpRequest.getEmail());
		account.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		account.setProvider(AuthProvider.LOCAL);

		Account result = accountRepository.save(account);

		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath().path("/user/me")
			.buildAndExpand(result.getId()).toUri();

		return ResponseEntity.created(location)
			.body(new ApiResponse(true, "User registered Successfully"));
	}
}
