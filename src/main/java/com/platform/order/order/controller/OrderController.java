package com.platform.order.order.controller;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.common.dto.cursor.CursorPageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.controller.dto.response.ReadMyOrderResponseDto;
import com.platform.order.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "주문 API")
@Validated
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {
	private final OrderService orderService;

	@Operation(summary = "주문", description = "주문할 상품 및 상품 수량을 입력받아 주문을 합니다.")
	@PostMapping
	public CreateOrderResponseDto order(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		CreateOrderRequestDto creatOrderRequest) {

		return orderService.order(principal.id(), creatOrderRequest);
	}

	@Operation(summary = "나의 주문 목록", description = "내가 주문했던 모든 주문 목록을 조회합니다.")
	@GetMapping
	public CursorPageResponseDto<ReadMyOrderResponseDto> getMyOrders(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@ParameterObject
		@Valid
		OrderPageRequestDto pageRequestDto) {

		return orderService.getMyOrders(principal.id(), pageRequestDto);
	}

	@Operation(summary = "주문 취소", description = "아직 배송처리 되기 전 주문했던 상품주문을 취소합니다.")
	@PatchMapping("/{orderProductId}")
	public Long cancel(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "주문상품 ID", required = true)
		@Positive
		@PathVariable
		Long orderProductId) {

		return orderService.cancel(principal.id(), orderProductId);
	}
}
