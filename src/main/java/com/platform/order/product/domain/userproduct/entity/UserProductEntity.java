package com.platform.order.product.domain.userproduct.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.user.domain.entity.UserEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_product")
@Entity
public class UserProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity wisher;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProductEntity product;

	@Builder.Default
	private LocalDate createdAt = LocalDate.now();

	public boolean isWisher(UserEntity wisher) {
		return this.wisher.equals(wisher);
	}

}
