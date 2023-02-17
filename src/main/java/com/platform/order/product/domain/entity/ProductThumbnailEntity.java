package com.platform.order.product.domain.entity;

import javax.persistence.Entity;
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
@Where(clause = "deleted=false")
@Table(name = "product_thunmnail_image")
@Entity
public class ProductThumbnailEntity extends BaseEntity {
	private String originName;
	private String name;
	private String path;
	private String extension;
	private Long size;
}
