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
@Table(name = "product")
@Entity
public class ProductEntity extends BaseEntity {
	private String name;
	private Long quantity;
	private Long price;
}
