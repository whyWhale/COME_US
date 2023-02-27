package com.platform.order.product.controller.dto.response;

public record WishProductResponseDto(
	Long id,
	String categoryCode,
	String categoryName,
	Long productId,
	String productName,
	Long price,
	String thumbnailPath,
	boolean isDisplay) {
}
