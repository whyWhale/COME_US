package com.platform.order.common.exception;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.exception.model.ErrorModel;
import com.platform.order.common.exception.protocal.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleBusinessException(BusinessException e) {
		log.error("Business exception occurred : {}", e.toString(), e);

		return createResponse(e.errorModel());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleDataIntegrityViolationException(
		DataIntegrityViolationException e) {
		log.warn("DataIntegrityViolation exception occurred : {}", e.toString(), e);

		return createResponse(ErrorCode.DATA_INTEGRITY_VIOLATION);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleConstraintViolation(ConstraintViolationException e) {
		log.warn("Constraint violation exception occurred: {}", e.toString(), e);

		return createResponse(ErrorCode.CONSTRAINT_VIOLATION);
	}

	@ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponse<ErrorModel>> handleBindException(BindException e) {
		log.warn("parameter binding exception occurred: {}", e.toString(), e);

		return createResponse(ErrorCode.BINDING_ERROR);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleInternalException(RuntimeException e) {
		log.warn("internal server error occurred: {}", e.toString(), e);

		return createResponse(ErrorCode.FATAL_ERROR);
	}

	private ResponseEntity<ErrorResponse<ErrorModel>> createResponse(ErrorModel errorCode) {
		ErrorResponse<ErrorModel> errorResponse = new ErrorResponse<>(errorCode);

		return new ResponseEntity<>(errorResponse, errorCode.getStatus());
	}
}
