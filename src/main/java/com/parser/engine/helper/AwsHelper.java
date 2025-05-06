package com.parser.engine.helper;

import com.parser.engine.dto.PreSignedUrlDto;
import com.parser.engine.entity.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

@Slf4j
@Component
public class AwsHelper {

	private final S3Client s3Client;
	private final SecretsManagerClient secretsManagerClient;

	@Value("${aws.s3.bucket_name}")
	private String bucketName;

	public AwsHelper(S3Client s3Client,
					 SecretsManagerClient secretsManagerClient) {
		this.s3Client = s3Client;
		this.secretsManagerClient = secretsManagerClient;
	}

	public File postToS3(String fileName, String awsKey, String contentType, InputStream inputStream) throws IOException {
		log.info("postToS3 fileName - {}, awsKey {}, contentType {}, bucket {}", fileName, awsKey, contentType, bucketName);
		File file = null;
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(awsKey)
				.contentType(contentType)
//				.acl(ObjectCannedACL.PRIVATE)
				.build();
		RequestBody requestBody = RequestBody.fromBytes(IoUtils.toByteArray(inputStream));
		PutObjectResponse result = s3Client.putObject(putObjectRequest, requestBody);
		if (Objects.nonNull(result)) {
			file = File.builder()
					.bucketName(bucketName)
					.assetId(result.eTag())
					.contentType(contentType)
					.s3Key(fileName)
					.awsKey(awsKey)
					.uploadedAt(ZonedDateTime.now())
					.build();
		}
		return file;
	}

	public byte[] getFromS3(String key) throws IOException {
		log.info("getFromS3 awsKey: {} bucket: {}", key, bucketName);
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
		ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
		return IoUtils.toByteArray(object);
	}

	public PreSignedUrlDto getPreSignedUrl(String key) {
		log.info("Generating the pre-signed url to download file from bucket: {} with key: {}", bucketName, key);
		var preSignedUrl = "";
		try (var preSigner = S3Presigner.create()) {
			var getObjectRequest = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.build();

			var getObjectPresignRequest = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofHours(1))
					.getObjectRequest(getObjectRequest)
					.build();

			PresignedGetObjectRequest presignedGetObjectRequest = preSigner.presignGetObject(getObjectPresignRequest);
			preSignedUrl = presignedGetObjectRequest.url().toString();

			log.info("Pre-Signed URL: {}", preSignedUrl);
		}
		return new PreSignedUrlDto(preSignedUrl);
	}

	public String getSecretForAws(String arn) {
		log.info("AWS Secret calling.... for arn {}", arn);
		try {
			var getSecretValueRequest = GetSecretValueRequest.builder()
					.secretId(arn)
					.build();

			var secretValueResult = secretsManagerClient.getSecretValue(getSecretValueRequest);
			log.info("secretValueResult is {}", secretValueResult);
			if (secretValueResult != null) {
				log.info("secretValueResult string {}", secretValueResult.secretString());
				return secretValueResult.secretString();
			}
		} catch (Exception e) {
			log.error("exception occurred while fetching secrets : ", e);
		}
		return null;
	}

}
