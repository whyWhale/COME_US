package com.platform.order.common.utils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

public class QueryDslUtils {

	/**
	 * pageable 에서 있는 정렬 기준을 queryDsl 정렬기준으로 바꾸기 위한 파싱 메서드
	 * @param order
	 * @param parent
	 * @param fieldName
	 * @return
	 */
	public static OrderSpecifier<?> getSortedColumn(Order order, Path<?> parent, String fieldName) {
		Path<Object> fieldPath = Expressions.path(Object.class, parent, fieldName);

		return new OrderSpecifier(order, fieldPath);
	}
}
