package com.platform.order.coupon.controller.dto.request.usercoupon;

import static com.platform.order.coupon.controller.dto.request.usercoupon.UserCouponPageRequestDto.UserCouponOrder.getDefaultOrder;
import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.Order.desc;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.platform.order.common.protocal.PageRequestDto;
import com.platform.order.coupon.domain.coupon.entity.CouponType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserCouponPageRequestDto extends PageRequestDto {
	private LocalDate lowerIssuedAt;
	private LocalDate upperIssuedAt;
	private LocalDate lowerExpiredAt;
	private LocalDate upperExpiredAt;
	private Boolean isUsable;
	private CouponType couponType;
	private List<UserCouponOrder> sorts;

	public UserCouponPageRequestDto(
		Integer page,
		Integer size,
		LocalDate lowerIssuedAt,
		LocalDate upperIssuedAt,
		LocalDate lowerExpiredAt,
		LocalDate upperExpiredAt,
		boolean isUsable,
		CouponType couponType,
		List<UserCouponOrder> sorts
	) {
		super(page, size);
		this.lowerIssuedAt = lowerIssuedAt;
		this.upperIssuedAt = upperIssuedAt;
		this.lowerExpiredAt = lowerExpiredAt;
		this.upperExpiredAt = upperExpiredAt;
		this.isUsable = isUsable;
		this.couponType = couponType;
		this.sorts = sorts;
	}

	public Pageable toPageable() {
		if (sorts == null) {
			return PageRequest.of(
				super.page,
				super.size,
				Sort.by(getDefaultOrder())
			);
		}

		List<Order> orders = sorts.stream()
			.map(UserCouponOrder::getPageableOrder)
			.toList();

		return PageRequest.of(
			super.page,
			super.size,
			Sort.by(orders)
		);
	}

	@Getter
	@AllArgsConstructor
	public enum UserCouponOrder {
		ISSUED_ASC("issuedAt", "asc"),
		ISSUED_DESC("issuedAt", "desc"),
		EXPIRED_ASC("expiredAt", "asc"),
		EXPIRED_DESC("expiredAt", "desc");

		String property;
		String direction;

		public Order getPageableOrder() {
			return this.direction.equals("desc") ? desc(this.getProperty()) : asc(this.getProperty());
		}

		public static Order getDefaultOrder() {
			return ISSUED_DESC.getPageableOrder();
		}
	}

}
