package com.parser.engine.exception;

import com.parser.engine.common.ExceptionCode;

import java.io.Serial;

public class AwsS3Exception extends ServiceException {

	@Serial
	private static final long serialVersionUID = 1L;

	public AwsS3Exception(ExceptionCode exceptionCode, String errorMessage) {
		super(exceptionCode, errorMessage);
	}
}
