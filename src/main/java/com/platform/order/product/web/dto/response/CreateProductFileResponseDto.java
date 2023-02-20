package com.platform.order.product.web.dto.response;

import java.util.List;

public record CreateProductFileResponseDto(thumbnailResponseDto thumbnailResponseDto, List<ImageResponseDto> imageResponseDtos) {

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
