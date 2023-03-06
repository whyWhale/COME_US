package com.platform.order.coupon.domain.usercoupon.entity;

import static java.text.MessageFormat.format;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.coupon.domain.coupon.entity.CouponEntity;
import com.platform.order.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_coupon")
@Entity
public class UserCouponEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	private CouponEntity coupon;

	private LocalDate issuedAt;

	@Builder.Default
	private boolean isUsable = true;

	public void use() {
		if (!isUsable) {
			throw new BusinessException(
				format("coupon {0} is already use", this.id),
				ErrorCode.ALREADY_USE_COUPON);
		}

		this.isUsable = false;
	}

	public void reActivate() {
		this.isUsable = true;
	}
}
