package com.platform.order.product.domain.repository.custom;

import org.springframework.data.repository.query.Param;

public interface CustomUserProductRepository {
	boolean existsByProductIdAndWisherId(@Param("productId") Long productId, @Param("wisherId") Long wisherId);
}
