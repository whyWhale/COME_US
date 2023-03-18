package com.platform.order.review.domain.review.repository;

import static com.platform.order.order.domain.orderproduct.entity.QOrderProductEntity.orderProductEntity;
import static com.platform.order.review.domain.review.entity.QReviewEntity.reviewEntity;
import static com.platform.order.review.domain.reviewimage.QReviewImageEntity.reviewImageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.platform.order.common.utils.QueryDslUtils;
import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.domain.review.entity.ReviewEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomReviewRepositoryImpl implements CustomReviewRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ReviewEntity> findByAllWithSorts(ReviewPageRequestDto page, Long productId) {
		Pageable pageable = page.toPageable();
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageable);

		//여기서 1:n 관계인데 페이징 처리가 제래도 델런지 	review 1개 3장 식 들어있으면
		// join 해서 1 리뷰 레코드당 이미지가 3개 붙어서 나온느데..
		List<ReviewEntity> reviews = queryFactory.selectFrom(reviewEntity)
			.join(reviewEntity.orderProduct, orderProductEntity)
			.where(orderProductEntity.product.id.eq(productId))
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.where(equalScore(page.getScore()))
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();
		List<Long> ids = reviews.stream()
			.map(ReviewEntity::getId)
			.toList();
		List<ReviewEntity> reviewWithImages = queryFactory.selectFrom(reviewEntity)
			.leftJoin(reviewEntity.images, reviewImageEntity).fetchJoin()
			.where(reviewEntity.id.in(ids))
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();

		JPAQuery<Long> count = queryFactory.select(reviewEntity.count())
			.from(reviewEntity)
			.join(reviewEntity.orderProduct, orderProductEntity)
			.where(orderProductEntity.product.id.eq(productId));

		return PageableExecutionUtils.getPage(reviewWithImages, pageable, count::fetchOne);
	}

	private BooleanExpression equalScore(Integer score) {
		return score == null ? null : reviewEntity.score.eq(score);
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (pageable.getSort().isEmpty()) {
			return orderSpecifiers;
		}

		pageable.getSort().stream().forEach(order -> {
			Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

			switch (order.getProperty()) {
				case "createdAt", "score" -> orderSpecifiers.add(
					QueryDslUtils.getSortedColumn(
						direction,
						reviewEntity,
						order.getProperty()
					)
				);
			}
		});

		return orderSpecifiers;
	}
}
