package com.platform.order.product.web.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.product.service.ProductService;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;
import com.platform.order.security.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductController {
	private final ProductService productService;
	@PostMapping
	public CreateProductResponseDto create(
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestPart CreateProductRequestDto createProductRequest,
		@RequestPart MultipartFile thumbnail,
		@RequestPart List<MultipartFile> images) {

		return productService.create(principal.id(),createProductRequest,thumbnail,images);
	}
}
