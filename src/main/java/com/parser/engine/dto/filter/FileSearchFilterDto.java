package com.parser.engine.dto.filter;

import com.parser.engine.enums.FileProcessingStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Data
@Builder
public class FileSearchFilterDto {

	private String fileName;
	private String fileType;
	private FileProcessingStatus fileProcessingStatus;
	private String uploadedBy;

	public boolean isAtleastOneFilterPresent() {
		String builder = fileName + fileType + uploadedBy;
		builder += Objects.nonNull(fileProcessingStatus) ? fileProcessingStatus.name() : "";
		return StringUtils.hasText(builder
				.replace("null", "")
				.replace("\\[\\]", ""));
	}
}
