package com.platform.order.product.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.DeleteProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.file.CreateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.product.file.UpdateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.ReadAllUserProductResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.WishUserProductResponseDto;
import com.platform.order.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping(value = "/api/products")
@RestController
public class ProductController {
	private final ProductService productService;

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping
	public CreateProductResponseDto create(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody CreateProductRequestDto createProductRequest) {

		return productService.create(principal.id(), createProductRequest);
	}

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping(value = "/file/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CreateProductFileResponseDto create(
		@PathVariable Long productId,
		@AuthenticationPrincipal JwtAuthentication principal,
		@RequestPart MultipartFile thumbnail,
		@Valid @Size(min = 1, max = 10) @RequestPart List<MultipartFile> images) {

		return productService.createFile(productId, principal.id(), thumbnail, images);
	}

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PatchMapping("/{productId}")
	public UpdateProductResponseDto upadte(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid @RequestBody UpdateProductRequestDto updateProductRequest,
		@PathVariable Long productId) {

		return productService.update(principal.id(), productId, updateProductRequest);
	}

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PatchMapping(value = "/file/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public UpdateProductFileResponseDto update(
		@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId,
		@RequestPart MultipartFile thumbnail,
		@Valid @Size(min = 1, max = 10) @RequestPart List<MultipartFile> images) {

		return productService.updateFile(productId, principal.id(), thumbnail, images);
	}

	@PreAuthorize("hasRole('ROLE_OWNER')")
	@DeleteMapping("/{productId}")
	public DeleteProductResponseDto delete(
		@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId) {

		return productService.delete(productId, principal.id());
	}

	@GetMapping("/{productId}")
	public ReadProductResponseDto read(@PathVariable Long productId) {
		return productService.read(productId);
	}

	@GetMapping
	public PageResponseDto<ReadAllProductResponseDto> readAll(@Valid ProductPageRequestDto pageRequest) {
		return productService.readAll(pageRequest);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping("/wish/{productId}")
	public WishUserProductResponseDto wish(
		@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long productId) {

		return productService.wish(productId, principal.id());
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/wish")
	public PageResponseDto<ReadAllUserProductResponseDto> readAllWishProducts(
		@AuthenticationPrincipal JwtAuthentication principal,
		@Valid WishUserProductPageRequestDto pageRequestDto) {

		return productService.readAllWishProducts(principal.id(), pageRequestDto);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@DeleteMapping("/wish/{userProductId}")
	public Long deleteWishProduct(@AuthenticationPrincipal JwtAuthentication principal,
		@PathVariable Long userProductId) {

		return productService.deleteWishProduct(principal.id(),userProductId);

	}
}
