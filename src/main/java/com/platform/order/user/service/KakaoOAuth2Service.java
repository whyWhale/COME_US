package com.platform.order.user.service;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.platform.order.common.security.exception.OAuth2Exception;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KakaoOAuth2Service implements OAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public Long save(OAuth2User oAuth2User, String provider) {

		String providerId = oAuth2User.getName();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		if (attributes == null) {
			throw new OAuth2Exception("oauth structure check...");
		}

		Map<String, Object> properties = (Map<String, Object>)attributes.get("properties");
		if (properties == null) {
			throw new OAuth2Exception("oauth structure check...");
		}

		String nickname = (String)properties.get("nickname");

		UserEntity savedUser = userRepository.save(UserEntity.builder()
			.providerId(providerId)
			.nickName(nickname)
			.provider(provider)
			.role(Role.USER)
			.build());

		return savedUser.getId();
	}

	@Override
	public boolean isMatchProvider(String provider) {
		return "kakao".equalsIgnoreCase(provider);
	}
}
