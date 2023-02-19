package com.platform.order.product.domain.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.BaseEntity;

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
	private CategoryEntity category;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private ProductThumbnailEntity productThumbnail;

}
