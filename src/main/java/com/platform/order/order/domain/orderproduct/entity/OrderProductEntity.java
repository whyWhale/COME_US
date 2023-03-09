package com.platform.order.order.domain.orderproduct.entity;

import java.text.MessageFormat;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.platform.order.common.supperentity.BaseEntity;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.coupon.domain.usercoupon.entity.UserCouponEntity;
import com.platform.order.order.domain.order.entity.OrderEntity;
import com.platform.order.order.domain.order.entity.OrderStatus;
import com.platform.order.product.domain.product.entity.ProductEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "order_product")
@Entity
public class OrderProductEntity extends BaseEntity {
	@NotNull
	@Positive
	private Long orderQuantity;

	@ManyToOne(fetch = FetchType.LAZY)
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProductEntity product;

	@OneToOne(fetch = FetchType.LAZY)
	private UserCouponEntity userCoupon;

	@Builder.Default
	@NotNull
	@Enumerated(EnumType.STRING)
	private OrderStatus status = OrderStatus.ACCEPT;

	public static OrderProductEntity create(ProductEntity product, Long orderQuantity) {
		product.decreaseStock(orderQuantity);

		return OrderProductEntity.builder()
			.product(product)
			.orderQuantity(orderQuantity)
			.build();
	}

	public void addOrder(OrderEntity order) {
		this.order = order;
	}

	public void applyCoupon(UserCouponEntity userCoupon) {
		this.userCoupon = userCoupon;
		userCoupon.use();
	}

	/**
	 *  단 하나의 상품에만 쿠폰 할인이 적용됨.
	 */
	public long getToTalPrice() {
		if (this.userCoupon == null) {
			return product.getPrice() * orderQuantity;
		}

		long discountPrice = userCoupon.getCoupon().discount(product.getPrice());

		if (orderQuantity == 1) {
			return discountPrice;
		}

		return discountPrice + product.getPrice() * (orderQuantity - 1);
	}

	public boolean isUseCoupon() {
		return this.userCoupon != null;
	}

	/**
	 * 쿠폰 사용은 ACCEPT 단계에서만 되돌려준다.
	 * @return
	 */
	public OrderProductEntity cancel() {
		if (!this.status.isCancel()) {
			throw new BusinessException(
				MessageFormat.format("delivery status : {0} is not cancel", this.status),
				ErrorCode.NOT_VALID_CANCEL
			);
		}

		if (this.userCoupon != null) {
			this.userCoupon.reActivate();
			this.userCoupon = null;
		}

		this.product.revert(orderQuantity);
		this.status = OrderStatus.CANCEL;

		return this;
	}
}
