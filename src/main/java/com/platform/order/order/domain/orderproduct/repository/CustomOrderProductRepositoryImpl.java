package com.platform.order.order.domain.orderproduct.repository;

import static com.platform.order.common.utils.QueryDslUtils.getSortedColumn;
import static com.platform.order.coupon.domain.coupon.entity.QCouponEntity.couponEntity;
import static com.platform.order.coupon.domain.usercoupon.entity.QUserCouponEntity.userCouponEntity;
import static com.platform.order.order.domain.order.entity.QOrderEntity.orderEntity;
import static com.platform.order.order.domain.orderproduct.entity.QOrderProductEntity.orderProductEntity;
import static com.platform.order.product.domain.product.entity.QProductEntity.productEntity;
import static com.platform.order.product.domain.productthumbnail.entity.QProductThumbnailEntity.productThumbnailEntity;
import static java.time.LocalTime.MAX;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOrderProductRepositoryImpl implements CustomOrderProductRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<OrderProductEntity> findMyAllWithConditions(Long authId, OrderPageRequestDto pageRequest) {
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageRequest.getSort());

		return queryFactory.selectFrom(orderProductEntity)
			.join(orderProductEntity.order, orderEntity)
			.join(orderProductEntity.product, productEntity).fetchJoin()
			.join(productEntity.productThumbnail, productThumbnailEntity).fetchJoin()
			.leftJoin(orderProductEntity.userCoupon, userCouponEntity).fetchJoin()
			.where(
				orderEntity.userId.eq(authId),
				lowerThanLastId(pageRequest.getLastId()),
				graterOrEqualThanMinPrice(pageRequest.getMinimumPrice()),
				lessOrEqualThanMaxPrice(pageRequest.getMaximumPrice()),
				likeProductName(pageRequest.getProductName()),
				createdAtUpperThanEquals(pageRequest.getUpperCreatedAt()),
				createdAtLowerThanEquals(pageRequest.getLowerCreatedAt())
			)
			.limit(pageRequest.getSize())
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();
	}

	private BooleanExpression lowerThanLastId(Long lastId) {
		return lastId == null ? null : orderProductEntity.id.lt(lastId);
	}

	private BooleanExpression graterOrEqualThanMinPrice(Long minPrice) {
		return minPrice == null ? null : productEntity.price.goe(minPrice);
	}

	private BooleanExpression lessOrEqualThanMaxPrice(Long maxPrice) {
		return maxPrice == null ? null : productEntity.price.loe(maxPrice);
	}

	private BooleanExpression likeProductName(String productName) {
		return StringUtils.hasText(productName) ? productEntity.name.like(productName + "%") : null;
	}

	private BooleanExpression createdAtLowerThanEquals(LocalDate lowerLocalDate) {
		return lowerLocalDate == null ? null : orderProductEntity.createdAt.loe(lowerLocalDate.atStartOfDay());
	}

	private BooleanExpression createdAtUpperThanEquals(LocalDate upperLocalDate) {
		return upperLocalDate == null ? null : orderProductEntity.createdAt.goe(upperLocalDate.atTime(MAX));
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Sort sorts) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (sorts.isEmpty()) {
			return orderSpecifiers;
		}

		sorts.forEach(order -> {
			Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

			switch (order.getProperty()) {
				case "createdAt", "id" -> orderSpecifiers.add(
					getSortedColumn(
						direction,
						orderProductEntity,
						order.getProperty())
				);
				case "price" -> orderSpecifiers.add(
					getSortedColumn(
						direction,
						couponEntity,
						order.getProperty())
				);
			}
		});

		return orderSpecifiers;
	}
}
