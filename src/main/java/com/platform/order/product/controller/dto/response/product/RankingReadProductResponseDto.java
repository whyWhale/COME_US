package com.platform.order.product.controller.dto.response.product;

public record RankingReadProductResponseDto(
	Long productId,
	String name,
	Long price,
	String categoryCode,
	String thumbnailPath
) {

}

