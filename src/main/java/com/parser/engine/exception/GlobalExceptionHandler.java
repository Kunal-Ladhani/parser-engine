package com.parser.engine.exception;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.response.ExceptionResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

/**
 * The type Global exception handler.
 *
 * @author Kunal Ladhani
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * General exception handler response entity.
	 *
	 * @param exp the exception
	 * @param req the web request
	 * @return the response entity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponseDto> generalExceptionHandler(Exception exp, WebRequest req) {
		log.error("General Exception occurred: {}", exp.getMessage(), exp);
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Method argument not valid exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponseDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exp, WebRequest req) {
		log.error("MethodArgumentNotValidException occurred: {}", exp.getMessage(), exp);
		var allErrors = exp.getBindingResult()
				.getAllErrors()
				.stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.toList();
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setExceptionCode(ExceptionCode.V101);
		ExceptionResponseDto.setErrorMessage(allErrors.toString());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({InvalidFileContentException.class, InvalidFileTypeException.class})
	public ResponseEntity<ExceptionResponseDto> invalidFileExceptionHandler(ServiceException exp, WebRequest req) {
		log.error("Exception occurred: {}", exp.getMessage(), exp);
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setExceptionCode(exp.getExceptionCode());
		ExceptionResponseDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * Resource does not exists exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler(ResourceDoesNotExistsException.class)
	public ResponseEntity<ExceptionResponseDto> resourceDoesNotExistsExceptionHandler(ResourceDoesNotExistsException exp, WebRequest req) {
		log.error("ResourceDoesNotExistsException occurred: {}", exp.getMessage(), exp);
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setExceptionCode(exp.getExceptionCode());
		ExceptionResponseDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.NOT_FOUND);
	}

	/**
	 * service exception handler response entity.
	 *
	 * @param exp the exception
	 * @return the response entity
	 */
	@ExceptionHandler({ServiceException.class,
			ResourceAlreadyExistsException.class,
			OperationNotAllowedException.class
	})
	public ResponseEntity<ExceptionResponseDto> serviceExceptionHandler(ServiceException exp, WebRequest req) {
		log.error("Exception occurred: {}", exp.getMessage(), exp);
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setExceptionCode(exp.getExceptionCode());
		ExceptionResponseDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ExceptionResponseDto> validationExceptionHandler(ValidationException exp, WebRequest req) {
		log.error("ValidationException occurred: {}", exp.getMessage(), exp);
		ExceptionResponseDto ExceptionResponseDto = new ExceptionResponseDto();
		ExceptionResponseDto.setTimestamp(new Date());
		ExceptionResponseDto.setExceptionCode(exp.getExceptionCode());
		ExceptionResponseDto.setErrorMessage(exp.getMessage());
		return new ResponseEntity<>(ExceptionResponseDto, HttpStatus.BAD_REQUEST);
	}

}