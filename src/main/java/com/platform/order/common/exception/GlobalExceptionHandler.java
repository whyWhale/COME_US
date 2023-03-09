package com.platform.order.common.exception;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.CustomFileIoException;
import com.platform.order.common.exception.custom.NotFoundResourceException;
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

	@ExceptionHandler(NotFoundResourceException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleNotFoundResourceException(NotFoundResourceException e) {
		log.error("NotFoundResource exception occurred : {}", e.toString(), e);

		return createResponse(ErrorCode.NOT_FOUND_RESOURCES);
	}

	@ExceptionHandler(CustomFileIoException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleCustomFileIoException(CustomFileIoException e) {
		log.error("File IO exception occurred : {}", e.toString(), e);

		return createResponse(ErrorCode.FILE_IO);
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

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse<ErrorModel>> handleAccessDeniedException(AccessDeniedException e) {
		log.warn("not authorization : {}", e.toString(), e);

		return createResponse(ErrorCode.NOT_AUTHORIZATION);
	}

	private ResponseEntity<ErrorResponse<ErrorModel>> createResponse(ErrorModel errorCode) {
		ErrorResponse<ErrorModel> errorResponse = new ErrorResponse<>(errorCode);

		return new ResponseEntity<>(errorResponse, errorCode.getStatus());
	}
}
