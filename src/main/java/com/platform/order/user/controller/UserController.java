package com.platform.order.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.platform.order.user.controller.request.SignUpUserRequestDto;
import com.platform.order.user.controller.response.SignUpUserResponseDto;
import com.platform.order.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public SignUpUserResponseDto regist(@RequestBody SignUpUserRequestDto signUpUser){
		return userService.register(signUpUser);
	}
}