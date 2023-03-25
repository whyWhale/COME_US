package com.platform.order.product.controller.dto.response.category;

import java.util.List;

public record CategoryResponseDto(
	String name,
	String code,
	List<SubCategoryResponse> subCategoryResponses
) {

	public record SubCategoryResponse(String name, String code) {

	}
}
