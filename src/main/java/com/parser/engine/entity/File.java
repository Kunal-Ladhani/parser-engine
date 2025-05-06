package com.parser.engine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "uploaded_file")
public class File extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "s3_key", nullable = false)
	private String s3Key;

	@Column(name = "aws_key", nullable = false)
	private String awsKey;

	@Column(name = "size")
	private Long size;

	@Column(name = "asset_id")
	private String assetId;

	@Column(name = "bucket_name")
	private String bucketName;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "uploaded_at")
	private ZonedDateTime uploadedAt;
}
