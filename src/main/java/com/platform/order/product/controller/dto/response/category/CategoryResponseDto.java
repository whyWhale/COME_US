package com.platform.order.product.controller.dto.response.category;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryResponseDto(
	@Schema(description = "카테고리 이름")
	String name,

	@Schema(description = "카테고리 코드")
	String code,
	List<SubCategoryResponse> subCategoryResponses
) {

	public record SubCategoryResponse(
		@Schema(description = "하위 카테고리 이름")
		String name,

		@Schema(description = "하위 카테고리 코드")
		String code) {

	}
}
