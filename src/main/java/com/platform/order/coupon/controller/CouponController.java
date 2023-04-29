package com.platform.order.coupon.controller;

import javax.validation.Valid;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.coupon.controller.dto.request.coupon.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.request.usercoupon.IssueUserCouponRequestDto;
import com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto;
import com.platform.order.coupon.controller.dto.response.coupon.CreateCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.IssueUserCouponResponseDto;
import com.platform.order.coupon.controller.dto.response.usercoupon.ReadUserCouponResponseDto;
import com.platform.order.coupon.service.CouponService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "쿠폰 API")
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController {
	private final CouponService couponService;

	@Operation(summary = "쿠폰 생성", description = "업주가 발행한 쿠폰입니다.")
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

	@Operation(summary = "쿠폰 발급", description = "사용자가 제한된 수량만큼 쿠폰을 발급받습니다.")
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
	public OffsetPageResponseDto<ReadUserCouponResponseDto> read(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@ParameterObject
		@Valid
		UserCouponPageRequestDto pageRequest) {

		return couponService.readAll(principal.id(), pageRequest);
	}
}
