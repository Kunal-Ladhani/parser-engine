package com.parser.engine.dto.filter;

import com.parser.engine.enums.FileProcessingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileSearchFilterDto {

	private String fileName;
	private String fileType;
	private FileProcessingStatus fileProcessingStatus;
	private String uploadedBy;

}
