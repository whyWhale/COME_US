package com.platform.order.product.web.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.product.service.ProductService;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.request.ProductPageRequestRequestDto;
import com.platform.order.product.web.dto.request.UpdateProductRequestDto;
import com.platform.order.product.web.dto.response.CreateProductFileResponseDto;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;
import com.platform.order.product.web.dto.response.DeleteProductResponseDto;
import com.platform.order.product.web.dto.response.ReadAllProductResponseDto;
import com.platform.order.product.web.dto.response.ReadProductResponseDto;
import com.platform.order.product.web.dto.response.UpdateProductFileResponseDto;
import com.platform.order.product.web.dto.response.UpdateProductResponseDto;
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
	public CreateProductFileResponseDto create(@PathVariable Long productId,
		@AuthenticationPrincipal JwtAuthentication principal, @RequestPart MultipartFile thumbnail,
		@Valid @Size(min = 1, max = 10) @RequestPart List<MultipartFile> images
	) {
		return productService.createFile(productId, principal.id(), thumbnail, images);
	}

	@PatchMapping("/{productId}")
	public UpdateProductResponseDto upadte(@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId, @Valid @RequestBody UpdateProductRequestDto updateProductRequest) {

		return productService.update(principal.id(), productId, updateProductRequest);
	}

	@PatchMapping(value = "/file/{productId}", consumes = "multipart/form-data")
	public UpdateProductFileResponseDto update(@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId, @RequestPart MultipartFile thumbnail,
		@Valid @Size(min = 1, max = 10) @RequestPart List<MultipartFile> images) {

		return productService.updateFile(productId, principal.id(), thumbnail, images);
	}

	@DeleteMapping("/{productId}")
	public DeleteProductResponseDto delete(@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId) {

		return productService.delete(productId, principal.id());
	}

	@GetMapping("/{productId}")
	public ReadProductResponseDto read(@PathVariable Long productId) {
		return productService.read(productId);
	}

	@GetMapping
	public PageResponseDto<ReadAllProductResponseDto> readAll(@Valid ProductPageRequestRequestDto pageRequest) {
		return productService.readAll(pageRequest);
	}

}
