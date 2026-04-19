package com.parser.engine.exception;

import com.parser.engine.common.ExceptionCode;

import java.io.Serial;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */
public class ValidationException extends ServiceException {

	@Serial
	private static final long serialVersionUID = 1L;

	public ValidationException(ExceptionCode exceptionCode, String errorMessage) {
		super(exceptionCode, errorMessage);
	}
}
