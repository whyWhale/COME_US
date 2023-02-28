package com.platform.order.coupon.domain.usercoupon.repository;

import static com.platform.order.coupon.domain.coupon.entity.QCouponEntity.couponEntity;
import static com.platform.order.coupon.domain.usercoupon.entity.QUserCouponEntity.userCouponEntity;
import static com.platform.order.user.domain.entity.QUserEntity.userEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.platform.order.common.utils.QueryDslUtils;
import com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto;
import com.platform.order.coupon.domain.coupon.entity.CouponType;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserCouponRepositoryImpl implements CustomUserCouponRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<UserCouponEntity> findAllWithConditions(UserCouponPageRequestDto page, Long authId) {
		Pageable pageable = page.toPageable();
		List<OrderSpecifier> orderSpecifiers = getAllOrderSpecifiers(pageable);

		List<UserCouponEntity> userCoupons = queryFactory.selectFrom(userCouponEntity)
			.join(userCouponEntity.coupon, couponEntity).fetchJoin()
			.join(userCouponEntity.user, userEntity).fetchJoin()
			.where(
				userEntity.id.eq(authId),
				issuedAtLowerThanEquals(page.getLowerIssuedAt()),
				issuedAtUpperThanEquals(page.getUpperIssuedAt()),
				expiredAtLowerThanEquals(page.getLowerExpiredAt()),
				expireAtUpperThanEquals(page.getUpperExpiredAt()),
				equalCouponType(page.getCouponType()),
				equalIsUsable(page.getIsUsable()))
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
			.fetch();

		JPAQuery<Long> count = queryFactory.select(userCouponEntity.count())
			.from(userCouponEntity)
			.join(userCouponEntity.coupon, couponEntity)
			.join(userCouponEntity.user, userEntity)
			.where(
				userEntity.id.eq(authId),
				issuedAtLowerThanEquals(page.getLowerIssuedAt()),
				issuedAtUpperThanEquals(page.getUpperIssuedAt()),
				expiredAtLowerThanEquals(page.getLowerExpiredAt()),
				expireAtUpperThanEquals(page.getUpperExpiredAt()),
				equalCouponType(page.getCouponType()),
				equalIsUsable(page.getIsUsable()));

		return PageableExecutionUtils.getPage(userCoupons, pageable, count::fetchOne);
	}

	private BooleanExpression equalCouponType(CouponType type) {
		return type == null ? null : couponEntity.type.eq(type);
	}

	private BooleanExpression equalIsUsable(Boolean isUsable) {
		return isUsable == null ? null : userCouponEntity.isUsable.eq(isUsable);
	}

	private BooleanExpression issuedAtLowerThanEquals(LocalDate lowerLocalDate) {
		return lowerLocalDate == null ? null : userCouponEntity.issuedAt.loe(lowerLocalDate);
	}

	private BooleanExpression issuedAtUpperThanEquals(LocalDate upperLocalDate) {
		return upperLocalDate == null ? null : userCouponEntity.issuedAt.goe(upperLocalDate);
	}

	private BooleanExpression expiredAtLowerThanEquals(LocalDate lowerLocalDate) {
		return lowerLocalDate == null ? null : couponEntity.expiredAt.loe(lowerLocalDate);
	}

	private BooleanExpression expireAtUpperThanEquals(LocalDate upperLocalDate) {
		return upperLocalDate == null ? null : couponEntity.expiredAt.goe(upperLocalDate);
	}

	private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

		if (pageable.getSort().isEmpty()) {
			return orderSpecifiers;
		}

		pageable.getSort().stream().forEach(order -> {
			Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

			switch (order.getProperty()) {
				case "issuedAt" -> orderSpecifiers.add(
					QueryDslUtils.getSortedColumn(direction, userCouponEntity, order.getProperty()));

				case "expiredAt" -> orderSpecifiers.add(
					QueryDslUtils.getSortedColumn(direction, couponEntity, order.getProperty()));
			}
		});

		return orderSpecifiers;
	}
}
