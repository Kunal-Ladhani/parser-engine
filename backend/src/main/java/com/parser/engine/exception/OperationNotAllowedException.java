package com.parser.engine.exception;


import com.parser.engine.common.ExceptionCode;

import java.io.Serial;

public class OperationNotAllowedException extends ValidationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OperationNotAllowedException(ExceptionCode exceptionCode, String errorMessage) {
        super(exceptionCode, errorMessage);
    }
}
