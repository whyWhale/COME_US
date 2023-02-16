package com.platform.order.order.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;
import com.platform.order.user.domain.entity.UserEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted=false")
@Table(name = "orders")
@Entity
public class OrderEntity extends BaseEntity {
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity user;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private DeliveryEntity delivery;

	@NotNull
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	public static OrderEntity create(UserEntity buyer, String address, String zipCode) {
		DeliveryEntity delivery = DeliveryEntity.builder()
			.address(address)
			.zipCode(zipCode)
			.build();

		return OrderEntity.builder()
			.user(buyer)
			.delivery(delivery)
			.status(OrderStatus.ACCEPT)
			.build();
	}
}
