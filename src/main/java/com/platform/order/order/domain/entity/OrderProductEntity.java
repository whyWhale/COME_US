package com.platform.order.order.domain.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.platform.order.product.domain.entity.ProductEntity;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "order_product")
@Entity
public class OrderProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY)
	ProductEntity product;

	@NotNull
	@Positive
	private Long orderQuantity;

	@NotNull
	@Positive
	private Long price;

	public Long getTotalPrice() {
		return this.price * this.orderQuantity;
	}
}
