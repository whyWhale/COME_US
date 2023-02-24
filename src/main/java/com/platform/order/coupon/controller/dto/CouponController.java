package com.platform.order.coupon.controller.dto;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController {
	private final CouponService couponService;

	@PostMapping
	public CreateCouponResponseDto create(@Valid @RequestBody CreateCouponRequestDto createCouponRequest) {

		return couponService.create(createCouponRequest);
	}
}
