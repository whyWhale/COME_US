package com.platform.order.product.controller.dto.response;

public record ReadUserProductResponseDto(Long id,
										 String categoryCode,
										 String categoryName,
										 Long productId,
										 String productName,
										 Long price,
										 String thumbnailPath) {
}
