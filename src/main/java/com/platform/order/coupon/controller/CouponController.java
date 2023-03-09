package com.platform.order.coupon.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.common.pagedto.offset.PageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.coupon.controller.dto.request.coupon.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.request.usercoupon.IssueUserCouponRequestDto;
import com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto;
import com.platform.order.coupon.controller.dto.response.coupon.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.IssueUserCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.ReadUserCouponResponseDto;
import com.platform.order.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController {
	private final CouponService couponService;

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping
	public CreateCouponResponseDto create(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		CreateCouponRequestDto createCouponRequest) {

		return couponService.create(principal.id(), createCouponRequest);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping("/issue")
	public IssueUserCouponResponseDto issue(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		IssueUserCouponRequestDto requestDto) {

		return couponService.issue(principal.id(), requestDto.couponId());
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/my")
	public PageResponseDto<ReadUserCouponResponseDto> read(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		UserCouponPageRequestDto pageRequest) {

		return couponService.readAll(principal.id(), pageRequest);
	}
}
