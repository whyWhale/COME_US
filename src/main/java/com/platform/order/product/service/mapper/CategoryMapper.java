package com.platform.order.product.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.platform.order.product.controller.dto.response.category.CategoryResponseDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;

@Component
public class CategoryMapper {
	public List<CategoryResponseDto> toCategoryResponses(List<CategoryEntity> categories) {
		return categories.stream()
			.filter(categoryEntity -> categoryEntity.getParent().isEmpty())
			.map(category -> new CategoryResponseDto(
				category.getName(),
				category.getCode(),
				toSubCategoryResponses(category)
			)).collect(Collectors.toList());
	}

	private List<CategoryResponseDto.SubCategoryResponse> toSubCategoryResponses(CategoryEntity category) {
		return category.getChilds().stream()
			.map(subCategoryEntity -> new CategoryResponseDto.SubCategoryResponse(
				subCategoryEntity.getName(),
				subCategoryEntity.getCode()
			)).toList();
	}
}
