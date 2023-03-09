package com.platform.order.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

/**
 * multipart type 인자의 파일이름 및 파일 사이즈 0일 때를 예방하기 위한 validation
 */
public class MultipartValidator implements ConstraintValidator<Multipart, MultipartFile> {
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		return value.getOriginalFilename() != null && value.getSize() != 0;
	}
}
