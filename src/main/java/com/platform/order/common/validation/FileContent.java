package com.platform.order.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileContentValidator.class)
public @interface FileContent {
	String message() default "파일이름과 크기가 있어야 합니다";

	Class[] groups() default {};

	Class[] payload() default {};
}
