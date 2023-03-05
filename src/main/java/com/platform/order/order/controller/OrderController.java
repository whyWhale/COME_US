package com.platform.order.order.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.common.protocal.CursorPageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.controller.dto.response.ReadMyOrderResponseDto;
import com.platform.order.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {
	private final OrderService orderService;

	@PostMapping
	public CreateOrderResponseDto order(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody CreateOrderRequestDto creatOrderRequest
	) {
		return orderService.order(principal.id(), creatOrderRequest);
	}

	@GetMapping
	public CursorPageResponseDto<ReadMyOrderResponseDto> getMyOrders(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid OrderPageRequestDto pageRequestDto
	) {

		return orderService.getMyOrders(principal.id(),pageRequestDto);
	}
}
