package com.rebwon.demojwt.security.oauth2;

import java.util.Optional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.rebwon.demojwt.domain.Account;
import com.rebwon.demojwt.domain.AccountRepository;
import com.rebwon.demojwt.domain.AuthProvider;
import com.rebwon.demojwt.exception.OAuth2AuthenticationProcessingException;
import com.rebwon.demojwt.security.UserAccount;
import com.rebwon.demojwt.security.oauth2.user.OAuth2UserInfo;
import com.rebwon.demojwt.security.oauth2.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2Service extends DefaultOAuth2UserService {
	private final AccountRepository accountRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		try{
			return processOAuth2User(userRequest, oAuth2User);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
		if(StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 Provider");
		}

		Optional<Account> optionalAccount = accountRepository.findByEmail(oAuth2UserInfo.getEmail());
		Account account;
		if(optionalAccount.isPresent()) {
			account = optionalAccount.get();
			if(!account.getProvider().equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
				log.info(userRequest.getClientRegistration().getRegistrationId());
				throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
					account.getProvider() + " account. Please use your " + account.getProvider() +
					" account to login.");
			}
			account = updateExistingAccount(account, oAuth2UserInfo);
		} else{
			account = registerNewAccount(userRequest, oAuth2UserInfo);
		}

		return UserAccount.create(account, oAuth2User.getAttributes());
	}

	private Account registerNewAccount(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
		Account account = new Account();

		account.setProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()));
		account.setProviderId(oAuth2UserInfo.getId());
		account.setName(oAuth2UserInfo.getName());
		account.setEmail(oAuth2UserInfo.getEmail());
		account.setImageUrl(oAuth2UserInfo.getImageUrl());

		return accountRepository.save(account);
	}

	private Account updateExistingAccount(Account existingAccount, OAuth2UserInfo oAuth2UserInfo) {
		existingAccount.setName(oAuth2UserInfo.getName());
		existingAccount.setImageUrl(oAuth2UserInfo.getImageUrl());
		return accountRepository.save(existingAccount);
	}
}
