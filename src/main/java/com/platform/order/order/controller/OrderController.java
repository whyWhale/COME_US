package com.platform.order.order.controller;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.order.service.OrderService;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {
	private final OrderService orderService;

	/**
	 * 단건 또는 다건으로 주문이 가능한 API
	 * @param principal
	 * @param creatOrderRequest
	 * @return
	 */
	@PostMapping
	public CreateOrderResponseDto placeOrder(@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody CreateOrderRequestDto creatOrderRequest) {

		return orderService.placeOrder(principal.id(),creatOrderRequest);
	}
}
