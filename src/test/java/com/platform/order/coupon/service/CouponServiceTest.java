package com.platform.order.coupon.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.platform.order.coupon.controller.dto.request.CreateCouponRequestDto;
import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.coupon.domain.repository.CouponRepository;
import com.platform.order.coupon.domain.repository.UserCouponRepository;
import com.platform.order.env.ServiceTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class CouponServiceTest extends ServiceTest {

	@InjectMocks
	CouponService couponService;

	@Mock
	CouponRepository couponRepository;

	@Mock
	CouponMapper couponMapper;

	@Mock
	UserRepository userRepository;

	@Mock
	UserCouponRepository userCouponRepository;

	UserEntity user;

	@BeforeEach
	public void setUp() {
		user = UserEntity
			.builder()
			.email("unitinid@google.com")
			.username("sopoju")
			.nickName("siua")
			.role(Role.OWNER)
			.password("1")
			.build();
	}

	@Test
	@DisplayName("쿠폰을 생성한다.")
	void testCreate() {
		//given
		CreateCouponRequestDto createCouponRequest = new CreateCouponRequestDto(CouponType.FIXED, 10000L, 1000L,
			LocalDate.now().plusDays(60));
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		//when
		couponService.create(1L, createCouponRequest);
		//then
		verify(couponRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("쿠폰을 발급받는다")
	void testIssue() {
		//given
		Long couponId = 1L;
		CouponEntity coupon = CouponEntity.builder()
			.user(user)
			.quantity(10L)
			.expiredAt(LocalDate.now().plusMonths(2))
			.amount(1000L)
			.type(CouponType.FIXED)
			.build();

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(couponRepository.findByIdAndExpiredAtAfter(any(),any())).willReturn(Optional.of(coupon));
		//when
		couponService.issue(1L, couponId);
		//then
		verify(userRepository, times(1)).findById(any());
		verify(couponRepository, times(1)).findByIdAndExpiredAtAfter(any(), any());
	}
}