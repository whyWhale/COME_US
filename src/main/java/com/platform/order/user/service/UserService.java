package com.platform.order.user.service;

import java.text.MessageFormat;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.user.controller.dto.request.SignUpUserRequestDto;
import com.platform.order.user.controller.dto.response.SignUpUserResponseDto;
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

}
