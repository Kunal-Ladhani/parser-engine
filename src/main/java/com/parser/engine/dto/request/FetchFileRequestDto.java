package com.parser.engine.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.parser.engine.enums.FileProcessingStatus;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FetchFileRequestDto {

	private String fileName;
	private String fileType;
	private FileProcessingStatus fileProcessingStatus;
	private String uploadedBy;

}