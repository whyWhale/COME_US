package com.platform.order.coupon.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.coupon.domain.entity.CouponEntity;
import com.platform.order.coupon.domain.entity.CouponType;
import com.platform.order.testenv.RepositoryTest;

class CouponRepositoryTest extends RepositoryTest {
	@Autowired
	CouponRepository couponRepository;

	CouponEntity coupon;

	@BeforeEach
	public void setUp() {
		coupon = couponRepository.save(
			CouponEntity.builder()
				.type(CouponType.FIXED)
				.quantity(100L)
				.amount(10000L)
				.expiredAt(LocalDate.now().plusMonths(1))
				.build()
		);
	}

	@AfterEach
	public void setDown() {
		couponRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("재고 감소 Native 쿼리로 수량이 정상적으로 차감되면 1을 리턴한다.")
	void testDecreaseQuantity() {
		//given
		//when
		boolean isAvailable = couponRepository.decreaseQuantity(coupon.getId()) == 1;
		//then
		assertThat(isAvailable).isTrue();
	}

	@Test
	@DisplayName("재고 감소 Native 쿼리로 수량이 차감되지 않으면 0을 리턴한다.")
	void failDecreaseQuantity() {
		//given
		CouponEntity notIssueCoupon = couponRepository.save(
			CouponEntity.builder()
				.type(CouponType.FIXED)
				.quantity(0L)
				.amount(10000L)
				.expiredAt(LocalDate.now().plusMonths(1))
				.build()
		);
		//when
		boolean isAvailable = couponRepository.decreaseQuantity(notIssueCoupon.getId()) == 1;

		//then
		assertThat(isAvailable).isFalse();
	}
}