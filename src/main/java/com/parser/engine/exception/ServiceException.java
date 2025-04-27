package com.parser.engine.exception;

import com.parser.engine.common.ExceptionCode;
import lombok.Getter;

import java.io.Serial;

/**
 * @author Kunal Ladhani
 * @since 1.0
 */
@Getter
public class ServiceException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	private final ExceptionCode exceptionCode;

	public ServiceException() {
		exceptionCode = null;
	}

	public ServiceException(ExceptionCode exceptionCode, String errorMessage) {
		super(errorMessage);
		this.exceptionCode = exceptionCode;
	}

	public ServiceException(ExceptionCode exceptionCode, Throwable cause, String errorMessage) {
		super(errorMessage, cause);
		this.exceptionCode = exceptionCode;
	}
}
