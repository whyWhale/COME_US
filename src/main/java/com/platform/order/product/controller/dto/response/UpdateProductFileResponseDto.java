package com.platform.order.product.controller.dto.response;

import java.util.List;

public record UpdateProductFileResponseDto(
	thumbnailResponseDto thumbnailResponseDto,
	List<ImageResponseDto> ImageResponseDtos) {

	public record thumbnailResponseDto(String fileName,
									   String originName,
									   String fileExtension,
									   String path,
									   Long size) {
	}

	public record ImageResponseDto(String fileName,
								   String originName,
								   String fileExtension,
								   String path,
								   Long size,
								   Long arrangement) {

	}
}
