package com.platform.order.common.storage;

import static com.platform.order.common.storage.FileSuffixPath.PRODUCT_THUMBNAIL;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.platform.order.testenv.IntegrationTest;

class StorageServiceTest extends IntegrationTest {

	@Autowired
	StorageService storageService;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Test
	void testUpload() throws IOException {
		// given
		String originalName = "test.png";
		Resource resource = resourceLoader.getResource("classpath:/static/" + originalName);
		var multipartFile = new MockMultipartFile("file", originalName, MediaType.IMAGE_PNG_VALUE,
			resource.getInputStream());
		FileSuffixPath fileSuffixPath = PRODUCT_THUMBNAIL;
		String extension = "png";
		// when
		String uploadPath = storageService.upload(multipartFile, fileSuffixPath, multipartFile.getName(), extension);
		// then
		Assertions.assertThat(uploadPath).isNotNull();
	}

	@Test
	void testDelete() {
		// given
		// when
		String deleteUrl = storageService.delete(PRODUCT_THUMBNAIL, "test.png");
		// then
		Assertions.assertThat(deleteUrl).isNotNull();
	}
}