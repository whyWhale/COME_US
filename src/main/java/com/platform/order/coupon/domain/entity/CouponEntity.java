package com.platform.order.coupon.domain.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.platform.order.common.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupon")
@Entity
public class CouponEntity extends BaseEntity {
	@Enumerated(EnumType.STRING)
	CouponType type;

	Long amount;

	Long quantity;

	LocalDate expiredAt;

}
