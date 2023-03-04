package com.platform.order.coupon.domain.coupon.entity;

public enum CouponType {
	PERCENT, FIXED;

	public long apply(Long price, Long amount) {
		return switch (this) {
			case FIXED -> price - amount;
			case PERCENT -> (long)(price * (1 + amount / 100.0));
		};
	}
}
