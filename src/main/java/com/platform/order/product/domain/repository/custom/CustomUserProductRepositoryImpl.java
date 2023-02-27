package com.platform.order.product.domain.repository.custom;

import static com.platform.order.product.domain.entity.QUserProductEntity.userProductEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserProductRepositoryImpl implements CustomUserProductRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByProductIdAndWisherId(Long productId, Long wisherId) {
		return queryFactory
			.selectOne()
			.from(userProductEntity)
			.where(userProductEntity.wisher.id.eq(wisherId),
				userProductEntity.product.id.eq(productId))
			.fetchFirst() != null;
	}
}
