package com.platform.order.common.utils;

import java.util.UUID;

public class FileUtils {
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.indexOf(".") + 1);
	}

	public static String generateFileName() {
		return UUID.randomUUID().toString();
	}

}
