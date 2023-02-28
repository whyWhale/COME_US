package com.platform.order.product.domain.userproduct.repository;

import static com.platform.order.product.domain.category.entity.QCategoryEntity.categoryEntity;
import static com.platform.order.product.domain.product.entity.QProductEntity.productEntity;
import static com.platform.order.product.domain.productthumbnail.entity.QProductThumbnailEntity.productThumbnailEntity;
import static com.platform.order.product.domain.userproduct.entity.QUserProductEntity.userProductEntity;
import static com.platform.order.user.domain.entity.QUserEntity.userEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.platform.order.common.utils.QueryDslUtils;
import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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

	@Override
	public Page<UserProductEntity> findAllWithCondtions(Long authId, WishUserProductPageRequestDto pageRequest) {
		Pageable pageable = pageRequest.toPageable();
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageable);

		List<UserProductEntity> userProducts = queryFactory.selectFrom(userProductEntity)
			.join(userProductEntity.product, productEntity).fetchJoin()
			.join(userProductEntity.wisher, userEntity)
			.join(productEntity.category, categoryEntity).fetchJoin()
			.join(productEntity.productThumbnail, productThumbnailEntity).fetchJoin()
			.where(
				userEntity.id.eq(authId),
				createdAtLowerThanEquals(pageRequest.getLowerCreatedAt()),
				createdAtUpperThanEquals(pageRequest.getUpperCreatedAt()))
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();

		JPAQuery<Long> count = queryFactory.select(userProductEntity.count())
			.from(userProductEntity)
			.join(userProductEntity.product, productEntity)
			.join(userProductEntity.wisher, userEntity)
			.join(productEntity.category, categoryEntity)
			.join(productEntity.productThumbnail, productThumbnailEntity)
			.where(
				userEntity.id.eq(authId),
				createdAtLowerThanEquals(pageRequest.getLowerCreatedAt()),
				createdAtUpperThanEquals(pageRequest.getUpperCreatedAt())
			);

		return PageableExecutionUtils.getPage(userProducts, pageable, count::fetchOne);
	}

	private BooleanExpression createdAtLowerThanEquals(LocalDate lowerLocalDate) {
		return lowerLocalDate == null ? null : userProductEntity.createdAt.loe(lowerLocalDate);
	}

	private BooleanExpression createdAtUpperThanEquals(LocalDate upperLocalDate) {
		return upperLocalDate == null ? null : userProductEntity.createdAt.goe(upperLocalDate);
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (pageable.getSort().isEmpty()) {
			return orderSpecifiers;
		}

		pageable.getSort().stream().forEach(order -> {
			Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
			QueryDslUtils.getSortedColumn(direction, userProductEntity, order.getProperty());
		});

		return orderSpecifiers;
	}
}
