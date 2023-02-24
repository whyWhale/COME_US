package com.platform.order.coupon;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.controller.dto.response.CreateCouponResponseDto;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.service.CouponService;
import com.platform.order.env.IntegrationTest;

public class CouponIntegrationTest extends IntegrationTest {
	@Autowired
	CouponService couponService;

	@Test
	@DisplayName("쿠폰을 생성한다.")
	void testCreate(){
	    //given
		CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 10000L, 1000L,
			LocalDate.now().plusDays(60));
	    //when
		CreateCouponResponseDto createCouponResponse = couponService.create(createCouponRequest);
		//then
		assertThat(createCouponResponse.couponType()).isEqualTo(createCouponRequest.type());
		assertThat(createCouponResponse.quantity()).isEqualTo(createCouponRequest.quantity());
		assertThat(createCouponResponse.amount()).isEqualTo(createCouponRequest.amount());
		assertThat(createCouponResponse.expiredAt()).isEqualTo(createCouponRequest.expiredAt());
	}
}
