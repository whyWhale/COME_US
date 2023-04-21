package com.platform.order.common.validation;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.utils.FileUtils;

public class FileContentValidator implements ConstraintValidator<FileContent, MultipartFile> {
	@Override
	public boolean isValid(MultipartFile content, ConstraintValidatorContext context) {
		if (content.getOriginalFilename() == null || content.getSize() == 0) {
			return false;
		}

		verifyExtension(content);

		return true;
	}

	private void verifyExtension(MultipartFile content) {
		String extension = FileUtils.getExtension(content.getOriginalFilename());
		MimeType mime = MimeType.of(extension);

		try (InputStream inputStream = content.getInputStream()) {
			byte[] bytes = new byte[mime.getBytesLengths()];
			inputStream.read(bytes);

			String hexadecimal = toHexadecimal(bytes);
			if (!mime.isMatch(hexadecimal)) {
				throw new BusinessException(
					format("{0} 확장자 파일의 시그니처가 올바르지 않습니다.", extension),
					ErrorCode.INVALID_CONTENT_SIGNATURE
				);
			}
		} catch (IOException e) {
			throw new BusinessException(
				format("{0} 파일을 읽어오는데 문제가 발생했습니다.", content.getOriginalFilename()),
				ErrorCode.FILE_IO
			);
		}
	}

	private String toHexadecimal(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte data : bytes) {
			sb.append(Integer.toString((data & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString().toUpperCase();
	}
}
