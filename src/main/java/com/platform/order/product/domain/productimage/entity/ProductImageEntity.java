package com.platform.order.product.domain.productimage.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.platform.order.common.supperentity.FileBaseEntity;
import com.platform.order.product.domain.product.entity.ProductEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_image")
@Entity
public class ProductImageEntity extends FileBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long arrangement;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProductEntity product;

	@Builder
	public ProductImageEntity(
		String originName,
		String fileName,
		String path,
		String extension,
		Long size,
		Long id,
		Long arrangement,
		ProductEntity product
	) {
		super(originName, fileName, path, extension, size);
		this.id = id;
		this.arrangement = arrangement;
		this.product = product;
	}
}
