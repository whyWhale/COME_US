package com.platform.order.product.domain.userproduct.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;

public interface CustomUserProductRepository {
	boolean existsByProductIdAndWisherId(@Param("productId") Long productId, @Param("wisherId") Long wisherId);

	Page<UserProductEntity> findAllWithCondtions(Long authId, WishUserProductPageRequestDto pageRequest);
}
