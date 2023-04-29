package com.platform.order.product.domain.product.repository.custom;

import org.springframework.data.domain.Page;

import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.domain.product.entity.ProductEntity;

public interface CustomProductRepository {
	Page<ProductEntity> findAllWithConditions(ProductPageRequestDto page);

	Page<ProductEntity> findAllWithConditions(ProductPageRequestDto page, String categoryCode);
}
