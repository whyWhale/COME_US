package com.platform.order.order.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stock {
	@Id
	@Column(name = "stock_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long quantity;
	@Column(name = "product_id", nullable = false)
	private Long productId;

	protected Stock() {
	}

	public Stock(Long productId, Long quantity) {
		this.quantity = quantity;
		this.productId = productId;
	}

	public Long getId() {
		return id;
	}

	public Long getQuantity() {
		return quantity;
	}

	public Long getProductId() {
		return productId;
	}
}
