package com.platform.order.order.domain.order.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
	ACCEPT("접수완료"),
	DELIVERING("배달중"),
	COMPLETE("완료"),

	CANCEL("접수취소"),
	REFUND("환불");

	private String status;

	public boolean isCancel() {
		return this == ACCEPT;
	}

}
