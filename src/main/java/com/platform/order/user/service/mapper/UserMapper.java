package com.platform.order.user.service.mapper;

import org.springframework.stereotype.Component;

import com.platform.order.user.controller.dto.response.SignUpUserResponseDto;
import com.platform.order.user.domain.entity.UserEntity;

@Component
public class UserMapper {
	public SignUpUserResponseDto toSignUpUserResponseDto(UserEntity registerdUser) {
		return new SignUpUserResponseDto(
			registerdUser.getId(),
			registerdUser.getUsername(),
			registerdUser.getEmail(),
			registerdUser.getNickName()
		);
	}
}
