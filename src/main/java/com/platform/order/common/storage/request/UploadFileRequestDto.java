package com.platform.order.common.storage.request;

import org.springframework.web.multipart.MultipartFile;

public record UploadFileRequestDto(
	String fileName,
	String extension,
	MultipartFile multipartFile

) {
}
