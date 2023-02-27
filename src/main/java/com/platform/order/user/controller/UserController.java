package com.platform.order.user.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.user.controller.dto.request.SignUpUserRequestDto;
import com.platform.order.user.controller.dto.response.SignUpUserResponseDto;
import com.platform.order.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

	private final UserService userService;
	@PostMapping
	public SignUpUserResponseDto register(@RequestBody SignUpUserRequestDto signUpUser){
		return userService.register(signUpUser);
	}
}
