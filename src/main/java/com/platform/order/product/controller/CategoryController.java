package com.platform.order.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.product.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "카테고리 API")
@RequiredArgsConstructor
@RequestMapping("/api/category")
@RestController
public class CategoryController {

	private final CategoryService categoryService;

	@Operation(summary = "전체 카테고리", description = "계층형 카테고리드릉 모두 가져옵니다.[캐싱 포함]")
	@GetMapping
	public void getCategories() {
		categoryService.getCategories();
	}

}
