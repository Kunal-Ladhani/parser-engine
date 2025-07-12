package com.parser.engine.entity;

import com.parser.engine.common.Constants.FileEntity;
import com.parser.engine.enums.FileProcessingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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

	@Column(name = FileEntity.UPLOADED_AT)
	private Instant uploadedAt;

	@Column(name = FileEntity.UPLOADED_BY)
	private String uploadedBy;
}
