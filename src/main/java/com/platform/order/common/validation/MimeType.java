package com.platform.order.common.validation;

import java.text.MessageFormat;
import java.util.Arrays;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MimeType {
	JPEG("FFD8FF"),
	JPG("FFD8FF"),
	GIF("474946"),
	PSD("384250"),
	PNG("89504E"),
	BMP("424D");

	private String signature;

	public boolean isMatch(String signature) {
		return this.signature.equals(signature);
	}

	public int getBytesLengths() {
		return this.signature.length() / 2;
	}

	public static MimeType of(String extension) {
		return Arrays.stream(MimeType.values())
			.filter(value -> value.toString().equals(extension))
			.findFirst()
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("extension: {}, 존재하지 않는 확장자입니다.", extension),
				ErrorCode.NOT_FOUND_CONTENT)
			);
	}
}
