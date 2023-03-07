package com.platform.order.common.storage;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StorageService {

	private final AmazonS3 s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.s3.suffix-url}")
	private String suffixUrl;

	public String upload(MultipartFile multipartFile, FileSuffixPath path, String fileName, String extension) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());
		objectMetadata.setContentLength(multipartFile.getSize());

		String key = generateKey(path, fileName, extension);
		try (InputStream inputStream = multipartFile.getInputStream()) {
			s3Client.putObject(
				new PutObjectRequest(
					"renewal-commerce",
					key,
					inputStream,
					objectMetadata
				)
			);
		} catch (IOException e) {
			delete(path, fileName + "." + extension);
			throw new BusinessException(
				MessageFormat.format("upload fail : {0}  ", multipartFile.getOriginalFilename()),
				ErrorCode.FILE_IO
			);
		}

		return s3Client.getUrl(bucket, key).toString();
	}

	public String delete(FileSuffixPath path, String fullFileName) {
		String key = generateKey(path, fullFileName);
		s3Client.deleteObject(bucket, key);

		return suffixUrl + key;
	}

	private String generateKey(FileSuffixPath path, String fileName) {
		return path.getPath() + fileName;
	}

	private String generateKey(FileSuffixPath path, String fileName, String extension) {
		return path.getPath() + fileName + "." + extension;
	}
}
