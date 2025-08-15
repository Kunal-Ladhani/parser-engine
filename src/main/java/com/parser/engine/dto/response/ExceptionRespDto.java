package com.parser.engine.dto.response;

import com.parser.engine.common.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionRespDto {

	private LocalDateTime timestamp;
	private ExceptionCode exceptionCode;
	private String errorMessage;
}