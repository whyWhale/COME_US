package com.platform.order.coupon.controller.dto.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Size;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.platform.order.common.protocal.PageRequestDto;
import com.platform.order.coupon.domain.entity.CouponType;

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

	@Size(max = 2)
	private List<UserCouponCondition> sorts;

	public UserCouponPageRequestDto(Integer page, Integer size, LocalDate lowerIssuedAt, LocalDate upperIssuedAt,
		LocalDate lowerExpiredAt, LocalDate upperExpiredAt, boolean isUsable, CouponType couponType,
		List<UserCouponCondition> sorts) {
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
			return PageRequest.of(super.page, super.size, Sort.by(Sort.Order.desc("issuedAt")));
		}

		List<Sort.Order> orders = sorts.stream()
			.map(UserCouponPageRequestDto.UserCouponCondition::getPageableOrder)
			.toList();

		return PageRequest.of(super.page, super.size, Sort.by(orders));
	}

	@Getter
	@AllArgsConstructor
	public enum UserCouponCondition {
		ISSUED_ASC("issuedAt", "asc"),
		ISSUED_DESC("issuedAt", "desc"),
		EXPIRED_ASC("expiredAt", "asc"),
		EXPIRED_DESC("expiredAt", "desc");
		String property;
		String direction;

		public Sort.Order getPageableOrder() {
			return this.direction.equals("desc") ?
				Sort.Order.desc(this.getProperty()) : Sort.Order.asc(this.getProperty());
		}
	}

}
