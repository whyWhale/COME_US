package com.platform.order.coupon.controller.dto.request.usercoupon;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record IssueUserCouponRequestDto(@NotNull @Positive Long couponId) {
}
