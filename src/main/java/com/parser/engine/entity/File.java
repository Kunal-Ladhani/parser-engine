package com.parser.engine.entity;

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
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "s3_key", nullable = false)
	private String s3Key;

	@Column(name = "aws_key", nullable = false)
	private String awsKey;

	@Column(name = "size_in_bytes")
	private Long size;

	@Column(name = "etag")
	private String etag;

	@Column(name = "bucket_name")
	private String bucketName;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "uploaded_at")
	private Instant uploadedAt;
}
