package com.platform.order.product.controller.dto.response.product;

public record RankingWishProductResponseDto(
	Long productId,
	String name,
	Long price,
	String categoryCode,
	String thumbnailPath
) {

}
