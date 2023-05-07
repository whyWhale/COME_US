package com.platform.order.common.security.oauth2.oauth2service;

import java.util.List;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.superentity.BaseEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuth2Service {

	private final UserRepository userRepository;
	private final List<OAuth2UserService> oAuth2UserServices;

	@Transactional
	public Long save(String provider, OAuth2User oAuth2User) {
		String providerId = oAuth2User.getName();

		return userRepository.findByProviderAndProviderId(provider, providerId)
			.map(BaseEntity::getId)
			.orElseGet(() ->
				oAuth2UserServices.stream()
					.filter(oAuth2UserService -> oAuth2UserService.isMatchProvider(provider))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("지원되는 서비스 OAUTH2인증이 없습니다."))
					.save(oAuth2User, provider)
			);
	}

}
