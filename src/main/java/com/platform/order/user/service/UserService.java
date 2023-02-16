package com.platform.order.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.user.controller.request.SignUpUserRequestDto;
import com.platform.order.user.controller.response.SignUpUserResponseDto;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserConverter userConverter;

	@Transactional
	public SignUpUserResponseDto register(SignUpUserRequestDto signUpRequestDto) {
		UserEntity user = UserEntity.builder()
			.username(signUpRequestDto.username())
			.password(passwordEncoder.encode(signUpRequestDto.password()))
			.nickName(signUpRequestDto.nickName())
			.email(signUpRequestDto.email())
			.role(signUpRequestDto.role())
			.build();

		UserEntity registedUser = userRepository.save(user);

		return userConverter.toSignUpUserResponseDto(registedUser);
	}

}