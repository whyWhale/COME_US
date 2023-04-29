package com.platform.order.product.domain.productimage.repository;

import java.util.List;

import com.platform.order.product.domain.productimage.entity.ProductImageEntity;

public interface CustomProductImageRepository {
	List<ProductImageEntity> saveAllInBulk(List<ProductImageEntity> productImages);

	void deleteBatchByProductId(Long productId);
}
