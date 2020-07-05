package com.rebwon.demojwt.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.rebwon.demojwt.domain.Account;
import com.rebwon.demojwt.domain.AccountRole;

public class UserAccount extends User implements OAuth2User {
	private Account account;
	private Map<String, Object> attributes;

	public UserAccount(Account account) {
		super(account.getEmail(), account.getPassword(), authorities(account.getRoles()));
		this.account = account;
	}

	private static Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
			.map(r -> new SimpleGrantedAuthority("ROLE" + r.name()))
			.collect(Collectors.toSet());
	}

	public static UserAccount create(Account account) {
		return new UserAccount(account);
	}

	public static UserAccount create(Account account, Map<String, Object> attributes) {
		UserAccount userAccount = UserAccount.create(account);
		userAccount.setAttributes(attributes);
		return userAccount;
	}

	public Long getId() {
		return this.account.getId();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getName() {
		return String.valueOf(account.getId());
	}
}
