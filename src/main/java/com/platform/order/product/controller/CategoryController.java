package com.platform.order.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.product.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/category")
@RestController
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public void getCategories() {
		categoryService.getCategories();
	}

}
