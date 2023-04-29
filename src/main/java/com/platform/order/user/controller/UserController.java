package com.platform.order.user.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.user.controller.dto.request.SignUpUserRequestDto;
import com.platform.order.user.controller.dto.response.SignUpUserResponseDto;
import com.platform.order.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 API")
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원 생성", description = "비밀번호 암호화는 단방향으로 이루어집니다.")
	@PostMapping
	public SignUpUserResponseDto register(
		@Valid
		@RequestBody
		SignUpUserRequestDto signUpUser
	) {
		return userService.register(signUpUser);
	}
}
