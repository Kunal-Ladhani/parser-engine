package com.parser.engine.dto.response;

import com.parser.engine.common.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponseDto {

	private Date timestamp;
	private ExceptionCode exceptionCode;
	private String errorMessage;
}