package com.platform.order.product.domain.entity;

import java.text.MessageFormat;

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
		return this.owner.equals(auth);
	}

	public ProductEntity update(String name, CategoryEntity category, Long price, Long quantity) {
		if (quantity == 0) {
			this.isDisplay = false;
		}

		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.category = category;

		return this;
	}

	public ProductThumbnailEntity updateThumbnail(ProductThumbnailEntity thumbnail) {
		if (this.productThumbnail == null) {
			throw new BusinessException(
				MessageFormat.format("because thumbnail is not exist, don't update thumbnail product id : {0}]",
					super.getId()),
				ErrorCode.EntityConstraint
			);
		}

		this.productThumbnail = thumbnail;

		return this.productThumbnail;
	}

	public void delete() {
		super.isDeleted = true;
		this.isDisplay = false;
	}
}
