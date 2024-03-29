package com.platform.order.coupon.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.coupon.controller.dto.request.coupon.CreateCouponRequestDto;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.coupon.domain.coupon.entity.CouponType;
import com.platform.order.coupon.domain.coupon.repository.CouponRepository;
import com.platform.order.coupon.domain.usercoupon.repository.UserCouponRepository;
import com.platform.order.coupon.service.mapper.CouponMapper;
import com.platform.order.testenv.ServiceTest;
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
	CouponEntity coupon;

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

		coupon = CouponEntity.builder()
			.user(user)
			.quantity(10L)
			.expiredAt(LocalDate.now().plusMonths(2))
			.amount(1000L)
			.type(CouponType.FIXED)
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
		int available = 1;

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(couponRepository.decreaseQuantity(any())).willReturn(available);
		given(couponRepository.findByIdAndExpiredAtAfter(any(), any())).willReturn(Optional.of(coupon));
		//when
		couponService.issue(1L, couponId);
		//then
		verify(userRepository, times(1)).findById(any());
		verify(couponRepository, times(1)).findByIdAndExpiredAtAfter(any(), any());
	}

	@Test
	@DisplayName("쿠폰 수량이 부족하면 비즈니스 예외가 발생한다.")
	void failIssueWithOutOfQuantity() {
		//given
		Long couponId = 1L;
		int NotAvailable = 0;

		given(userRepository.findById(any())).willReturn(Optional.of(user));

		//when
		//then
		assertThatThrownBy(() -> {
			couponService.issue(1L, couponId);
		}).isInstanceOf(BusinessException.class);
	}
}