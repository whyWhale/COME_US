package com.platform.order.product.controller.dto.response.product;

public record CreateThumbnailResponseDto(String fileName,
										 String originName,
										 String fileExtension,
										 String path,
										 Long size) {
}
