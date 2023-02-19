package com.platform.order.common.utils;

public class FileUtils {
	public static String getExtension(String fileName) {
		return fileName.substring(fileName.indexOf(".")+1);
	}

}
