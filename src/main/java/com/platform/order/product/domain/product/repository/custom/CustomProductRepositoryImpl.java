package com.platform.order.product.domain.product.repository.custom;

import static com.platform.order.product.domain.category.entity.QCategoryEntity.categoryEntity;
import static com.platform.order.product.domain.product.entity.QProductEntity.productEntity;
import static com.platform.order.product.domain.productthumbnail.entity.QProductThumbnailEntity.productThumbnailEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.platform.order.common.utils.QueryDslUtils;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ProductEntity> findAllWithConditions(
		ProductPageRequestDto page
	) {
		Pageable pageable = page.toPageable();
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageable);

		List<ProductEntity> products = queryFactory.selectFrom(productEntity)
			.join(productEntity.productThumbnail, productThumbnailEntity)
			.join(productEntity.category, categoryEntity)
			.fetchJoin()
			.where(
				graterOrEqualThanMinPrice(page.getMinimumPrice()),
				lessOrEqualThanMaxPrice(page.getMaximumPrice()),
				likeProductName(page.getName()))
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();

		JPAQuery<Long> count = queryFactory.select(productEntity.count())
			.from(productEntity)
			.innerJoin(productEntity.productThumbnail, productThumbnailEntity)
			.innerJoin(productEntity.category, categoryEntity)
			.where(
				graterOrEqualThanMinPrice(page.getMinimumPrice()),
				lessOrEqualThanMaxPrice(page.getMaximumPrice()),
				likeProductName(page.getName()));

		return PageableExecutionUtils.getPage(products, pageable, count::fetchOne);
	}

	@Override
	public Page<ProductEntity> findAllWithConditions(ProductPageRequestDto page, String categoryCode) {
		Pageable pageable = page.toPageable();
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageable);

		List<ProductEntity> products = queryFactory.selectFrom(productEntity)
			.join(productEntity.productThumbnail, productThumbnailEntity)
			.join(productEntity.category, categoryEntity)
			.fetchJoin()
			.where(
				productEntity.category.code.eq(categoryCode),
				graterOrEqualThanMinPrice(page.getMinimumPrice()),
				lessOrEqualThanMaxPrice(page.getMaximumPrice()),
				likeProductName(page.getName()))
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();

		JPAQuery<Long> count = queryFactory.select(productEntity.count())
			.from(productEntity)
			.innerJoin(productEntity.productThumbnail, productThumbnailEntity)
			.innerJoin(productEntity.category, categoryEntity)
			.where(
				graterOrEqualThanMinPrice(page.getMinimumPrice()),
				lessOrEqualThanMaxPrice(page.getMaximumPrice()),
				likeProductName(page.getName()));

		return PageableExecutionUtils.getPage(products, pageable, count::fetchOne);
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

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (pageable.getSort().isEmpty()) {
			return orderSpecifiers;
		}

		pageable.getSort().forEach(order -> {
			Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
			orderSpecifiers.add(QueryDslUtils.getSortedColumn(direction, productEntity, order.getProperty()));
		});

		return orderSpecifiers;
	}
}
