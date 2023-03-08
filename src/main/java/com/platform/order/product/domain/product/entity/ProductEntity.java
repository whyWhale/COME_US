package com.platform.order.product.domain.product.entity;

import static java.text.MessageFormat.format;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.user.domain.entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted=false AND is_display=true")
@Table(name = "product")
@Entity
public class ProductEntity extends BaseEntity {
	private String name;
	private Long quantity;
	private Long price;
	private boolean isDisplay;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity owner;

	@ManyToOne(fetch = FetchType.LAZY)
	private CategoryEntity category;

	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, orphanRemoval = true)
	private ProductThumbnailEntity productThumbnail;

	public ProductThumbnailEntity addThumbnail(ProductThumbnailEntity thumbnailEntity) {
		this.productThumbnail = thumbnailEntity;

		return this.productThumbnail;
	}

	public boolean isOwner(UserEntity auth) {
		return this.owner.getId().equals(auth.getId());
	}

	public ProductEntity update(ProductEntity updateRequestProduct) {
		if (updateRequestProduct.quantity == 0) {
			this.isDisplay = false;
		}

		this.name = updateRequestProduct.name;
		this.price = updateRequestProduct.price;
		this.quantity = updateRequestProduct.quantity;
		this.category = updateRequestProduct.category;

		return this;
	}

	public ProductThumbnailEntity updateThumbnail(ProductThumbnailEntity thumbnail) {
		this.productThumbnail = null;
		this.productThumbnail = thumbnail;

		return this.productThumbnail;
	}

	public void delete() {
		super.isDeleted = true;
		this.isDisplay = false;
	}

	public void decreaseStock(Long orderQuantity) {
		if (this.quantity < orderQuantity) {
			throw new BusinessException(
				format("product {0} is Out Of Quantity {1}", this.id, this.quantity),
				ErrorCode.OUT_OF_QUANTITY);
		}

		this.quantity -= orderQuantity;
	}

	public void revert(Long orderQuantity) {
		this.quantity += orderQuantity;
	}
}
