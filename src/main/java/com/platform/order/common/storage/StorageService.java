package com.platform.order.common.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StorageService {

	public String upload(MultipartFile multipartFile, FileSuffixPath path, String fileName, String extension) {

		return "upload path";
	}

	private String generateKey(FileSuffixPath path, String fileName, String extension) {
		return path + fileName + "." + extension;
	}
}
