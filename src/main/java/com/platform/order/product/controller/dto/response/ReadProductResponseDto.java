package com.platform.order.product.controller.dto.response;

import java.util.List;

public record ReadProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String thumbnailPath,
	String category,
	List<String> imagePaths) {
}
