package com.parser.engine.dto.response;

import com.parser.engine.common.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRespDto {

	private Date timestamp;
	private ExceptionCode exceptionCode;
	private String errorMessage;
}