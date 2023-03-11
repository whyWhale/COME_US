package com.platform.order.common.scheduler;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.coupon.domain.coupon.repository.CouponRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Component
public class ScheduleService {

	private final CouponRepository couponRepository;

	/**
	 * 쿠폰 만료 시간이 지난 쿠폰을 자동으로 삭제한다.
	 * 그리고 만료된 쿠폰을 가지고 있는 사용자 쿠폰을 모두 사용할 수 없게 한다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void expireCoupon() {
		couponRepository.expireAllBatch(LocalDate.now());
	}
}

