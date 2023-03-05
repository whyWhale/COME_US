package com.platform.order.order.domain.order.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;
import com.platform.order.order.domain.delivery.entity.DeliveryEntity;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;

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
	private Long userId;

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private DeliveryEntity delivery;

	@Builder.Default
	@OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
	private Set<OrderProductEntity> orderproducts = new HashSet<>();

	public static OrderEntity create(Long userId, String address, String zipCode) {
		DeliveryEntity delivery = DeliveryEntity.builder()
			.address(address)
			.zipCode(zipCode)
			.build();

		return OrderEntity.builder()
			.userId(userId)
			.delivery(delivery)
			.build();
	}

	public List<OrderProductEntity> addOrderProduct(List<OrderProductEntity> orderProducts) {
		orderProducts.forEach(orderProduct -> orderProduct.addOrder(this));
		this.orderproducts.addAll(orderProducts);

		return orderProducts;
	}
}
