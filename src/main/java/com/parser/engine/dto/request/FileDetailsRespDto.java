package com.parser.engine.dto.request;

import com.parser.engine.common.Constants;
import com.parser.engine.entity.Auditable;
import com.parser.engine.enums.FileProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class FileDetailsRespDto extends Auditable {

	@Column(name = Constants.FileEntity.FILE_NAME)
	private String fileName;

	@Column(name = Constants.FileEntity.FILE_TYPE)
	private String fileType;

	@Column(name = Constants.FileEntity.FILE_PROCESSING_STATUS)
	@Enumerated(EnumType.STRING)
	private FileProcessingStatus fileProcessingStatus;

	@Column(name = Constants.FileEntity.S3_KEY, nullable = false)
	private String s3Key;

	@Column(name = Constants.FileEntity.AWS_KEY, nullable = false)
	private String awsKey;

	@Column(name = Constants.FileEntity.SIZE_IN_BYTES)
	private Long size;

	@Column(name = Constants.FileEntity.ETAG)
	private String etag;

	@Column(name = Constants.FileEntity.BUCKET_NAME)
	private String bucketName;

	@Column(name = Constants.FileEntity.CONTENT_TYPE)
	private String contentType;

	@Column(name = Constants.FileEntity.UPLOADED_AT)
	private Instant uploadedAt;

	@Column(name = Constants.FileEntity.UPLOADED_BY)
	private String uploadedBy;

}
