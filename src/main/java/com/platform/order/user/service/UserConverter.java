package com.platform.order.user.service;

import org.springframework.stereotype.Component;

import com.platform.order.user.controller.response.SignUpUserResponseDto;
import com.platform.order.user.domain.entity.UserEntity;

@Component
public class UserConverter {
	public SignUpUserResponseDto toSignUpUserResponseDto(UserEntity registerdUser) {
		return new SignUpUserResponseDto(
			registerdUser.getId(),
			registerdUser.getUsername(),
			registerdUser.getEmail(),
			registerdUser.getNickName()
		);
	}
}
