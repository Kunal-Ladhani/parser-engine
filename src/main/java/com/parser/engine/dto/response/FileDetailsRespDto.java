package com.parser.engine.dto.response;

import com.parser.engine.entity.Auditable;
import com.parser.engine.enums.FileProcessingStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class FileDetailsRespDto extends Auditable {

	private UUID id;

	private String fileName;

	private String fileType;

	private FileProcessingStatus fileProcessingStatus;

	private Long size;

	private String contentType;

	private Instant uploadedAt;

	private String uploadedBy;

}
