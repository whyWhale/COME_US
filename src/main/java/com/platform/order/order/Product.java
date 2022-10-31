package com.platform.order.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private long quantity;

	protected Product() {
	}

	@Builder
	public Product(Long id, String name, Long quantity) {
		this.id = id;
		this.name = name;
		this.quantity = quantity;
	}

	public void decrese(long amount) {
		if (quantity - amount < 0) {
			log.info("재고량 감소 실패..");
			throw new RuntimeException("재고량 보다 더 많이 주문할 수 없습니다.");
		}

		quantity -= amount;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Long getQuantity() {
		return quantity;
	}
}
