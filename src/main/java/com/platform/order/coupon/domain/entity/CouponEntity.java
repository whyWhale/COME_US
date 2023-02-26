package com.platform.order.coupon.domain.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;
import com.platform.order.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted=false")
@Table(name = "coupon")
@Entity
public class CouponEntity extends BaseEntity {
	@Enumerated(EnumType.STRING)
	private CouponType type;

	private Long amount;

	private Long quantity;

	private LocalDate expiredAt;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity user;
}
