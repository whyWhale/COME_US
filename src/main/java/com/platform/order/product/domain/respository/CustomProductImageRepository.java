package com.platform.order.product.domain.respository;

import java.util.List;

import com.platform.order.product.domain.entity.ProductImageEntity;

public interface CustomProductImageRepository {
	List<ProductImageEntity> saveAllInBulk(List<ProductImageEntity> productImages);

	void deleteBatchByProductId(Long productId);
}
