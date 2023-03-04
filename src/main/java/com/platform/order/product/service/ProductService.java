package com.platform.order.product.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.common.storage.FileSuffixPath;
import com.platform.order.common.storage.StorageService;
import com.platform.order.common.utils.FileUtils;
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
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.domain.productimage.entity.ProductImageEntity;
import com.platform.order.product.domain.productimage.repository.ProductImageRepository;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.product.domain.userproduct.repository.UserProductRepository;
import com.platform.order.product.service.redis.ProductRedisService;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ProductImageRepository imageRepository;
	private final UserProductRepository userProductRepository;
	private final StorageService storageService;
	private final ProductRedisService productRedisService;
	private final ProductMapper productMapper;

	@Transactional
	public CreateProductResponseDto create(Long authId, CreateProductRequestDto createProductRequest) {
		UserEntity owner = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);

		CategoryEntity category = categoryRepository.findByCode(createProductRequest.categoryCode()).orElseThrow(() ->
			new NotFoundResourceException(
				MessageFormat.format("category code :{0} is not found.", createProductRequest.categoryCode()),
				ErrorCode.NOT_FOUND_RESOURCES)
		);

		ProductEntity savedProductEntity = productRepository.save(ProductEntity.builder()
			.name(createProductRequest.name())
			.quantity(createProductRequest.quantity())
			.price(createProductRequest.price())
			.isDisplay(true)
			.category(category)
			.owner(owner)
			.build());

		return productMapper.toCreateProductResponseDto(savedProductEntity);
	}

	@Transactional
	public CreateProductFileResponseDto createFile(
		Long productId,
		Long authId,
		MultipartFile thumbnail,
		List<MultipartFile> images) {

		ProductEntity foundProduct = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES));
		UserEntity auth = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				MessageFormat.format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		String originalThumbnailName = thumbnail.getOriginalFilename();
		String thumbnailExtension = FileUtils.getExtension(originalThumbnailName);
		String thumbnailFileName = UUID.randomUUID().toString();
		long thumbnailSize = thumbnail.getSize();
		String thumbnailPath = storageService.upload(thumbnail, FileSuffixPath.PRODUCT_THUMBNAIL, thumbnailFileName,
			thumbnailExtension);

		ProductThumbnailEntity productThumbnail = foundProduct.addThumbnail(ProductThumbnailEntity.builder()
			.name(thumbnailFileName)
			.originName(thumbnail.getOriginalFilename())
			.path(thumbnailPath)
			.size(thumbnailSize)
			.extension(thumbnailExtension)
			.build());

		productRepository.save(foundProduct);

		AtomicLong arrange = new AtomicLong();
		List<ProductImageEntity> productImageEntities = images.stream().map(multipartFile -> {
			String imageFileName = UUID.randomUUID().toString();
			String originalImageName = multipartFile.getOriginalFilename();
			String imageFileExtension = FileUtils.getExtension(originalImageName);
			long imageSize = multipartFile.getSize();
			String imagePath = storageService.upload(multipartFile, FileSuffixPath.PRODUCT_IMAGE, imageFileName,
				imageFileExtension);

			return ProductImageEntity.builder()
				.originName(originalImageName)
				.name(imageFileName)
				.path(imagePath)
				.extension(imageFileExtension)
				.size(imageSize)
				.arrangement(arrange.incrementAndGet())
				.product(foundProduct)
				.build();
		}).toList();

		imageRepository.saveAllInBulk(productImageEntities);

		return productMapper.productFileResponseDto(productThumbnail, productImageEntities);
	}

	@Transactional
	public UpdateProductResponseDto update(Long authId, Long productId, UpdateProductRequestDto updateProductRequest) {
		UserEntity auth = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);
		ProductEntity foundProduct = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES)
			);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				MessageFormat.format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		CategoryEntity category = categoryRepository.findByCode(updateProductRequest.categoryCode()).orElseThrow(() ->
			new NotFoundResourceException(
				MessageFormat.format("category code :{0} is not found.", updateProductRequest.categoryCode()),
				ErrorCode.NOT_FOUND_RESOURCES)
		);

		ProductEntity updatedProduct = foundProduct.update(updateProductRequest.name(), category,
			updateProductRequest.price(),
			updateProductRequest.quantity());

		return productMapper.toUpdateProductResponseDto(updatedProduct);
	}

	@Transactional
	public UpdateProductFileResponseDto updateFile(Long productId, Long authId, MultipartFile thumbnail,
		List<MultipartFile> images) {
		UserEntity auth = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);
		ProductEntity foundProduct = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES));

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				MessageFormat.format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		ProductThumbnailEntity pastThumbnail = foundProduct.getProductThumbnail();
		storageService.delete(pastThumbnail.getPath());

		String originalThumbnailName = thumbnail.getOriginalFilename();
		String thumbnailExtension = FileUtils.getExtension(originalThumbnailName);
		String thumbnailFileName = UUID.randomUUID().toString();
		long thumbnailSize = thumbnail.getSize();
		String thumbnailPath = storageService.upload(thumbnail, FileSuffixPath.PRODUCT_THUMBNAIL, thumbnailFileName,
			thumbnailExtension);

		ProductThumbnailEntity newThunmbnail = foundProduct.updateThumbnail(ProductThumbnailEntity.builder()
			.name(thumbnailFileName)
			.originName(thumbnail.getOriginalFilename())
			.path(thumbnailPath)
			.size(thumbnailSize)
			.extension(thumbnailExtension)
			.build());

		imageRepository.findByProduct(foundProduct).forEach(image -> storageService.delete(image.getPath()));
		imageRepository.deleteBatchByProductId(foundProduct.getId());

		AtomicLong arrange = new AtomicLong();
		List<ProductImageEntity> productImages = images.stream().map(multipartFile -> {
			String imageFileName = UUID.randomUUID().toString();
			String originalImageName = multipartFile.getOriginalFilename();
			String imageFileExtension = FileUtils.getExtension(originalImageName);
			long imageSize = multipartFile.getSize();
			String imagePath = storageService.upload(multipartFile, FileSuffixPath.PRODUCT_IMAGE, imageFileName,
				imageFileExtension);

			return ProductImageEntity.builder()
				.originName(multipartFile.getOriginalFilename())
				.name(imageFileName)
				.path(imagePath)
				.extension(imageFileExtension)
				.size(imageSize)
				.arrangement(arrange.incrementAndGet())
				.product(foundProduct)
				.build();
		}).toList();

		imageRepository.saveAllInBulk(productImages);

		return productMapper.toUpdateProductFileResponseDto(newThunmbnail, productImages);
	}

	@Transactional
	public DeleteProductResponseDto delete(Long productId, Long authId) {
		ProductEntity foundProduct = productRepository.findByIdWithCategory(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES));
		UserEntity auth = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				MessageFormat.format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		foundProduct.delete();

		return productMapper.toDeleteProductResponseDto(foundProduct);
	}

	public ReadProductResponseDto read(Long productId, String visitor) {
		ProductEntity foundProduct = productRepository.findByIdWithCategoryAndThumbnail(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES));
		List<ProductImageEntity> images = imageRepository.findByProduct(foundProduct);

		productRedisService.increaseViewCount(productId, visitor);
		long wishCount = productRedisService.getWishCount(productId);

		return productMapper.toReadProductResponseDto(foundProduct, images, wishCount);
	}

	public PageResponseDto<ReadAllProductResponseDto> readAll(ProductPageRequestDto pageRequest) {
		Page<ProductEntity> productsPage = productRepository.findAllWithConditions(pageRequest);

		if (productsPage.getContent().isEmpty()) {
			return productMapper.toNoContentPageResponseDto(productsPage);
		}

		Map<Long, Long> wishCounts = productsPage.getContent().stream()
			.map(product -> new Long[] {product.getId(), productRedisService.getWishCount(product.getId())})
			.collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));

		return productMapper.toNoContentPageResponseDto(productsPage, wishCounts);
	}

	@Transactional
	public WishUserProductResponseDto wish(Long productId, Long authId) {
		boolean isAlreadyWish = userProductRepository.existsByProductIdAndWisherId(productId, authId);

		if (isAlreadyWish) {
			throw new BusinessException(
				MessageFormat.format("is Already wish product : {0}", productId),
				ErrorCode.ALREADY_WISH);
		}

		UserEntity auth = userRepository.findById(authId).orElseThrow(() -> new NotFoundResourceException(
			MessageFormat.format("user id :{0} is not found.", authId),
			ErrorCode.NOT_FOUND_RESOURCES)
		);
		ProductEntity foundProduct = productRepository.findByIdWithCategoryAndThumbnail(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("product id :{0} is not found.", productId),
				ErrorCode.NOT_FOUND_RESOURCES));
		UserProductEntity userProduct = userProductRepository.save(
			UserProductEntity.builder()
				.wisher(auth)
				.product(foundProduct)
				.build());
		productRedisService.increaseWishCount(productId);

		return productMapper.toWishProductResponseDto(userProduct);
	}

	public PageResponseDto<ReadAllUserProductResponseDto> readAllWishProducts(Long authId,
		WishUserProductPageRequestDto pageRequestDto) {
		Page<UserProductEntity> pageUserProduct = userProductRepository.findAllWithCondtions(authId, pageRequestDto);

		return productMapper.toPageResponse(pageUserProduct);
	}

	public Long deleteWishProduct(Long authId, Long userProductId) {
		UserProductEntity userProductEntity = userProductRepository.findByIdAndWisherId(userProductId, authId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("user product id :{0} is not found.", userProductId),
				ErrorCode.NOT_FOUND_RESOURCES));

		userProductRepository.delete(userProductEntity);
		productRedisService.decreaseWishCount(userProductEntity.getProduct().getId());

		return userProductId;
	}

}
