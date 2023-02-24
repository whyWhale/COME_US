package com.platform.order.coupon.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.domain.repository.CouponRepository;
import com.platform.order.env.ServiceTest;

class CouponServiceTest extends ServiceTest {

	@InjectMocks
	CouponService couponService;

	@Mock
	CouponRepository couponRepository;

	@Mock
	CouponMapper couponMapper;

	@Test
	@DisplayName("쿠폰을 생성한다.")
	void testCreate(){
	    //given
		CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 10000L, 1000L,
			LocalDate.now().plusDays(60));
	    //when
	    couponService.create(createCouponRequest);
	    //then
		verify(couponRepository, times(1)).save(any());
	}
}