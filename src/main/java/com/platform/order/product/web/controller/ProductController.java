package com.platform.order.product.web.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.product.service.ProductService;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;
import com.platform.order.product.web.dto.response.ProductFileResponseDto;
import com.platform.order.security.JwtAuthentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/api/products")
@RestController
public class ProductController {
	private final ProductService productService;

	@PostMapping
	public CreateProductResponseDto create(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody CreateProductRequestDto createProductRequest) {

		return productService.create(principal.id(), createProductRequest);
	}

	@PostMapping(value = "/file/{productId}", consumes = "multipart/form-data")
	public ProductFileResponseDto create(@PathVariable Long productId, @RequestPart MultipartFile thumbnail,
		@Valid @Size(min = 1, max = 10) @RequestPart List<MultipartFile> images
	) {

		return productService.createFile(productId, thumbnail, images);
	}
}
