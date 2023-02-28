package com.platform.order.product.controller.dto.response.userproduct;

public record ReadAllUserProductResponseDto(Long id,
											String categoryCode,
											String categoryName,
											Long productId,
											String productName,
											Long price,
											String thumbnailPath) {
}
