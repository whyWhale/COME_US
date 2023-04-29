package com.platform.order.product.service;

import static com.platform.order.common.storage.FileSuffixPath.PRODUCT_IMAGE;
import static com.platform.order.common.storage.FileSuffixPath.PRODUCT_THUMBNAIL;
import static com.platform.order.common.utils.FileUtils.generateFileName;
import static com.platform.order.common.utils.FileUtils.getExtension;
import static java.text.MessageFormat.format;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.storage.AwsStorageService;
import com.platform.order.order.service.redis.OrderRedisService;
import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.controller.dto.response.product.CreateProductImagesResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductImageResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.ReadAllUserProductResponseDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.domain.productimage.entity.ProductImageEntity;
import com.platform.order.product.domain.productimage.repository.ProductImageRepository;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.product.domain.userproduct.repository.UserProductRepository;
import com.platform.order.product.service.mapper.ProductMapper;
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
	private final AwsStorageService awsStorageService;
	private final ProductRedisService productRedisService;
	private final OrderRedisService orderRedisService;
	private final ProductMapper productMapper;

	@Transactional
	public CreateProductResponseDto create(Long authId, CreateProductRequestDto createProductRequest) {
		UserEntity auth = getAuth(authId);
		CategoryEntity category = getCategory(createProductRequest.categoryCode());
		ProductEntity product = productMapper.toProduct(createProductRequest, auth, category);
		ProductEntity savedProductEntity = productRepository.save(product);

		return productMapper.toCreateProductResponseDto(savedProductEntity);
	}

	@Transactional
	public UpdateProductResponseDto update(
		Long authId,
		Long productId,
		UpdateProductRequestDto updateProductRequest
	) {
		UserEntity auth = getAuth(authId);
		ProductEntity foundProduct = getProduct(productId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		CategoryEntity foundCategory = getCategory(updateProductRequest.categoryCode());
		ProductEntity updateRequestProduct = productMapper.toProduct(updateProductRequest, foundCategory);
		ProductEntity updatedProduct = foundProduct.update(updateRequestProduct);

		return productMapper.toUpdateProductResponseDto(updatedProduct);
	}

	@Transactional
	public Long delete(Long productId, Long authId) {
		ProductEntity foundProduct = productRepository.findByIdWithCategory(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("product id :{0} is not found.", productId))
			);
		UserEntity auth = getAuth(authId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		foundProduct.delete();

		return foundProduct.getId();
	}

	public ReadProductResponseDto read(Long productId, String visitor) {
		ProductEntity foundProduct = productRepository.findByIdWithCategoryAndThumbnail(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("product id :{0} is not found.", productId)
			));
		List<ProductImageEntity> images = imageRepository.findByProduct(foundProduct);

		productRedisService.increaseViewCount(productId, visitor);
		long wishCount = productRedisService.getWishCount(productId);

		return productMapper.toReadProductResponseDto(foundProduct, images, wishCount);
	}

	public OffsetPageResponseDto<ReadAllProductResponseDto> readAll(ProductPageRequestDto pageRequest,
		String categoryCode) {
		Page<ProductEntity> productsPage = productRepository.findAllWithConditions(pageRequest);

		if (productsPage.getContent().isEmpty()) {
			return productMapper.toNoContentPageResponseDto(productsPage);
		}

		Map<Long, Long> wishCounts = productsPage.getContent().stream()
			.map(product -> new Long[] {product.getId(), productRedisService.getWishCount(product.getId())})
			.collect(Collectors.toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));

		return productMapper.toContentPageResponseDto(productsPage, wishCounts);
	}

	@Transactional
	public Long wishProduct(Long productId, Long authId) {
		boolean isAlreadyWish = userProductRepository.existsByProductIdAndWisherId(productId, authId);

		if (isAlreadyWish) {
			throw new BusinessException(
				format("is Already wish product : {0}", productId),
				ErrorCode.ALREADY_WISH);
		}

		UserEntity auth = getAuth(authId);
		ProductEntity foundProduct = getProduct(productId);
		UserProductEntity userProduct = productMapper.toUserProduct(auth, foundProduct);
		UserProductEntity savedUserProduct = userProductRepository.save(userProduct);
		productRedisService.increaseWishCount(productId);

		return savedUserProduct.getProduct().getId();
	}

	public OffsetPageResponseDto<ReadAllUserProductResponseDto> readAllWishProducts(
		Long authId,
		WishUserProductPageRequestDto pageRequestDto
	) {
		Page<UserProductEntity> pageUserProduct = userProductRepository.findAllWithCondtions(authId, pageRequestDto);

		return productMapper.toPageResponse(pageUserProduct);
	}

	public Long unWishProduct(Long authId, Long userProductId) {
		UserProductEntity userProductEntity = userProductRepository.findByIdAndWisherId(userProductId, authId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("user product id :{0} is not found.", userProductId)
			));

		userProductRepository.delete(userProductEntity);
		productRedisService.decreaseWishCount(userProductEntity.getProduct().getId());

		return userProductId;
	}

	@Transactional
	public CreateThumbnailResponseDto createThumbnail(
		Long productId,
		Long authId,
		MultipartFile thumbnailFile
	) {
		ProductEntity foundProduct = getProduct(productId);
		UserEntity auth = getAuth(authId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		String thumbnailExtension = getExtension(thumbnailFile.getOriginalFilename());
		String thumbnailFileName = generateFileName();
		String thumbnailPath = awsStorageService.upload(
			thumbnailFile,
			PRODUCT_THUMBNAIL,
			thumbnailFileName,
			thumbnailExtension);

		ProductThumbnailEntity thumbnail = foundProduct.addThumbnail(ProductThumbnailEntity.builder()
			.fileName(thumbnailFileName)
			.originName(thumbnailFile.getOriginalFilename())
			.path(thumbnailPath)
			.size(thumbnailFile.getSize())
			.extension(thumbnailExtension)
			.build());

		return productMapper.toCreateThumbnailResponse(thumbnail);
	}

	@Transactional
	public List<CreateProductImagesResponseDto> createImages(
		Long productId,
		Long authId,
		List<MultipartFile> multipartImage
	) {
		ProductEntity foundProduct = getProduct(productId);
		UserEntity auth = getAuth(authId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		AtomicLong arrangement = new AtomicLong();
		List<ProductImageEntity> productImageEntities = multipartImage.stream().map(image -> {
			String imageFileName = generateFileName();
			String originalImageName = image.getOriginalFilename();
			String imageFileExtension = getExtension(originalImageName);
			long imageSize = image.getSize();
			String imagePath = awsStorageService.upload(
				image,
				PRODUCT_IMAGE,
				imageFileName,
				imageFileExtension
			);

			return ProductImageEntity.builder()
				.originName(originalImageName)
				.fileName(imageFileName)
				.path(imagePath)
				.extension(imageFileExtension)
				.size(imageSize)
				.arrangement(arrangement.incrementAndGet())
				.product(foundProduct)
				.build();
		}).toList();

		List<ProductImageEntity> productImages = imageRepository.saveAllInBulk(productImageEntities);

		return productMapper.toCreateProductImagesResponses(productImages);
	}

	@Transactional
	public UpdateProductThumbnailResponseDto updateThumbnail(
		Long productId,
		Long authId,
		MultipartFile multipartThumbnail
	) {
		UserEntity auth = getAuth(authId);
		ProductEntity foundProduct = getProduct(productId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		ProductThumbnailEntity pastThumbnail = foundProduct.getProductThumbnail();
		awsStorageService.delete(PRODUCT_THUMBNAIL, pastThumbnail.generateFullFileName());

		String thumbnailExtension = getExtension(multipartThumbnail.getOriginalFilename());
		String thumbnailFileName = generateFileName();
		String thumbnailPath = awsStorageService.upload(
			multipartThumbnail,
			PRODUCT_THUMBNAIL,
			thumbnailFileName,
			thumbnailExtension);

		ProductThumbnailEntity newThumbnail = foundProduct.updateThumbnail(ProductThumbnailEntity.builder()
			.fileName(thumbnailFileName)
			.originName(multipartThumbnail.getOriginalFilename())
			.path(thumbnailPath)
			.size(multipartThumbnail.getSize())
			.extension(thumbnailExtension)
			.build());

		return productMapper.toUpdateProductThumbnailResponse(newThumbnail);
	}

	public List<UpdateProductImageResponseDto> updateImages(
		Long productId,
		Long authId,
		List<MultipartFile> multipartImages
	) {

		UserEntity auth = getAuth(authId);
		ProductEntity foundProduct = getProduct(productId);

		if (!foundProduct.isOwner(auth)) {
			throw new BusinessException(
				format("product owner is not match. auth id : {0}", authId),
				ErrorCode.NOT_OWNER
			);
		}

		imageRepository.findByProduct(foundProduct)
			.forEach(image -> awsStorageService.delete(PRODUCT_IMAGE, image.getPath()));
		imageRepository.deleteBatchByProductId(foundProduct.getId());

		AtomicLong arrange = new AtomicLong();
		List<ProductImageEntity> productImages = multipartImages.stream().map(multipartFile -> {
			String imageFileName = generateFileName();
			String originalImageName = multipartFile.getOriginalFilename();
			String imageFileExtension = getExtension(originalImageName);
			long imageSize = multipartFile.getSize();
			String imagePath = awsStorageService.upload(multipartFile, PRODUCT_IMAGE, imageFileName,
				imageFileExtension);

			return ProductImageEntity.builder()
				.originName(multipartFile.getOriginalFilename())
				.fileName(imageFileName)
				.path(imagePath)
				.extension(imageFileExtension)
				.size(imageSize)
				.arrangement(arrange.incrementAndGet())
				.product(foundProduct)
				.build();
		}).toList();

		List<ProductImageEntity> productImageEntities = imageRepository.saveAllInBulk(productImages);

		return productMapper.toUpdateProductImageResponseDtos(productImageEntities);
	}

	private ProductEntity getProduct(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("product id :{0} is not found.", productId)
			));
	}

	private UserEntity getAuth(Long authId) {
		return userRepository.findById(authId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("user id :{0} is not found.", authId)
			));
	}

	private CategoryEntity getCategory(String code) {
		return categoryRepository.findByCode(code)
			.orElseThrow(() -> new NotFoundResourceException(
				format("category code :{0} is not found.", code)
			));
	}
}
