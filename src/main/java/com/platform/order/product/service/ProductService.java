package com.platform.order.product.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.exception.custom.ErrorCode;
import com.platform.order.common.exception.custom.NotFoundResource;
import com.platform.order.common.utils.FileUtils;
import com.platform.order.product.domain.entity.CategoryEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.entity.ProductImageEntity;
import com.platform.order.product.domain.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.respository.CategoryRepository;
import com.platform.order.product.domain.respository.ProductImageRepository;
import com.platform.order.product.domain.respository.ProductRepository;
import com.platform.order.product.domain.respository.ProductThumbnailRepository;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {
	private final ProductRepository productRepository;
	private final ProductThumbnailRepository thumbnailRepository;
	private final CategoryRepository categoryRepository;
	private final ProductImageRepository productImageRepository;
	private final ProductMapper productMapper;

	@Transactional
	public CreateProductResponseDto create(Long userId, CreateProductRequestDto createProductRequest,
		MultipartFile thumbnail, List<MultipartFile> productImages) {
		String categoryCode = createProductRequest.categoryCode();
		CategoryEntity category = categoryRepository.findByCode(categoryCode).orElseThrow(() ->
			new NotFoundResource(
				MessageFormat.format("category code :{0} is not found.", categoryCode),
				ErrorCode.NOT_FOUND_RESOURCES)
		);

		String originalThumbnailName = thumbnail.getOriginalFilename();
		String thumbnailExtension = FileUtils.getExtension(originalThumbnailName);
		String thumbnailFileName = UUID.randomUUID().toString();
		long thumbnailSize = thumbnail.getSize();
		String thumbnailPath = "upload path"; // upload

		ProductThumbnailEntity savedThumbnail = thumbnailRepository.save(ProductThumbnailEntity.builder()
			.name(thumbnailFileName)
			.originName(thumbnail.getOriginalFilename())
			.path(thumbnailPath)
			.size(thumbnailSize)
			.extension(thumbnailExtension)
			.build());

		ProductEntity savedProductEntity = productRepository.save(ProductEntity.builder()
			.name(createProductRequest.name())
			.quantity(createProductRequest.quantity())
			.price(createProductRequest.price())
			.isDisplay(true)
			.category(category)
			.productThumbnail(savedThumbnail)
			.build());

		AtomicLong arrange = new AtomicLong();

		List<ProductImageEntity> productImageEntities = productImages.stream().map(multipartFile -> {
			String imageFileName = UUID.randomUUID().toString();
			String originalImageName = multipartFile.getOriginalFilename();
			String imageFileExtension = FileUtils.getExtension(originalImageName);
			String imagePath = "image path"; // upload
			long imageSize = multipartFile.getSize();

			return ProductImageEntity.builder()
				.originName(multipartFile.getOriginalFilename())
				.name(imageFileName)
				.path(imagePath)
				.extension(imageFileExtension)
				.size(imageSize)
				.arrangement(arrange.incrementAndGet())
				.product(savedProductEntity)
				.build();
		}).toList();

		productImageRepository.saveAllInBulk(productImageEntities);

		return productMapper.toCreateProductResponseDto(savedProductEntity);
	}

}
