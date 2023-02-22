package com.platform.order.product.web.dto.response;

import java.util.List;

public record ReadProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String thumbnailPath,
	String category,
	List<String> imagePaths
	) {
}
