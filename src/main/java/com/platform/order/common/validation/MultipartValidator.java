package com.platform.order.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class MultipartValidator implements ConstraintValidator<Multipart, MultipartFile> {
	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		return value.getOriginalFilename() != null && value.getSize() != 0;
	}
}
