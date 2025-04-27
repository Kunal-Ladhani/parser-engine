package com.parser.engine.exception;


import com.parser.engine.common.ExceptionCode;

import java.io.Serial;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */
public class ResourceDoesNotExistsException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceDoesNotExistsException(ExceptionCode exceptionCode, String errorMessage) {
        super(exceptionCode, errorMessage);
    }
}
