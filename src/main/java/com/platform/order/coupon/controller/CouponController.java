package com.platform.order.coupon.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.IssueCouponResponseDto;
import com.platform.order.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController {
	private final CouponService couponService;

	@PostMapping
	public CreateCouponResponseDto create(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody CreateCouponRequestDto createCouponRequest) {

		return couponService.create(principal.id(),createCouponRequest);
	}

	@PostMapping("/issue")
	public IssueCouponResponseDto issue(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody @NotNull @Positive Long couponId) {

		return couponService.issue(principal.id(), couponId);
	}
}
