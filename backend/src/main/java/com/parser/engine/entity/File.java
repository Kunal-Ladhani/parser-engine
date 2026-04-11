package com.parser.engine.entity;

import com.parser.engine.common.Constants.FileEntity;
import com.parser.engine.enums.FileProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "uploaded_file")
public class File extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = FileEntity.ID, updatable = false, nullable = false)
	private UUID id;

	@Column(name = FileEntity.FILE_NAME)
	private String fileName;

	@Column(name = FileEntity.FILE_TYPE)
	private String fileType;

	@Column(name = FileEntity.FILE_PROCESSING_STATUS)
	@Enumerated(EnumType.STRING)
	private FileProcessingStatus fileProcessingStatus;

	@Column(name = FileEntity.S3_KEY, nullable = false)
	private String s3Key;

	@Column(name = FileEntity.AWS_KEY, nullable = false)
	private String awsKey;

	@Column(name = FileEntity.SIZE_IN_BYTES)
	private Long size;

	@Column(name = FileEntity.ETAG)
	private String etag;

	@Column(name = FileEntity.BUCKET_NAME)
	private String bucketName;

	@Column(name = FileEntity.CONTENT_TYPE)
	private String contentType;

	// -------------------------------------------------- PROCESSED --------------------------------------------
	@Column(name = FileEntity.PROCESSED_AT)
	private LocalDateTime processedAt;

	@Column(name = FileEntity.PROCESSED_BY)
	private String processedBy;

	@Builder.Default
	@Column(name = FileEntity.IS_PROCESSED, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isProcessed = false;

	// --------------------------------------------------- UPLOAD --------------------------------------------
	@Column(name = FileEntity.UPLOADED_AT)
	private LocalDateTime uploadedAt;

	@Column(name = FileEntity.UPLOADED_BY)
	private String uploadedBy;

	// --------------------------------------------------- DELETE --------------------------------------------
	@Column(name = FileEntity.DELETED_AT)
	private LocalDateTime deletedAt;

	@Column(name = FileEntity.DELETED_BY)
	private String deletedBy;

	@Builder.Default
	@Column(name = FileEntity.IS_DELETED, columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean isDeleted = false;
}
