package com.platform.order.common.storage.response;

import org.springframework.web.multipart.MultipartFile;

public record UploadFileResponseDto(
	String fileName,
	String path,
	String extension,
	MultipartFile multipartFile
) {
}
