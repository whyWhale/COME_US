package com.platform.order.common.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileSuffixPath {
	PRODUCT_THUMBNAIL("product/thumbnail/"),
	PRODUCT_IMAGE("product/image/"),
	REVIEW_IMAGE("review/image/");

	private String path;
}
