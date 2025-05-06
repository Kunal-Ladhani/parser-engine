package com.parser.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class PreSignedUrlDto {
	private String preSignedUrl;
}