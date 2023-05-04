package com.platform.order.user.service;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.superentity.BaseEntity;
import com.platform.order.user.controller.dto.request.SignUpUserRequestDto;
import com.platform.order.user.controller.dto.response.SignUpUserResponseDto;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;
import com.platform.order.user.service.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final List<OAuth2UserService> oAuth2UserServices;

	@Transactional
	public SignUpUserResponseDto register(SignUpUserRequestDto signUpRequestDto) {
		UserEntity user = UserEntity.builder()
			.username(signUpRequestDto.username())
			.password(passwordEncoder.encode(signUpRequestDto.password()))
			.nickName(signUpRequestDto.nickName())
			.email(signUpRequestDto.email())
			.role(signUpRequestDto.role())
			.build();

		UserEntity registeredUser = userRepository.save(user);

		return userMapper.toSignUpUserResponseDto(registeredUser);
	}

	public UserEntity getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("user id:{0} is not found.", userId)
			));
	}

	@Transactional
	public Long saveForOAuth2(String provider, OAuth2User oAuth2User) {
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
