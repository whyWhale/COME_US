package com.platform.order.product.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.product.controller.dto.response.category.CategoryResponseDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.service.mapper.CategoryMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;

	@Cacheable("categories")
	public List<CategoryResponseDto> getCategories() {
		List<CategoryEntity> categories = categoryRepository.findAllOrderByCode();

		return categoryMapper.toCategoryResponses(categories);
	}

}
