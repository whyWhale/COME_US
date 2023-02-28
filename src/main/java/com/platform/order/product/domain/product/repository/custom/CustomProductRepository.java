package com.platform.order.product.domain.product.repository.custom;

import org.springframework.data.domain.Page;

import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;

public interface CustomProductRepository {
	Page<ProductEntity> findAllWithConditions(ProductPageRequestDto page);
}