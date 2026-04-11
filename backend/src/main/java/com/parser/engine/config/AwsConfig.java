package com.parser.engine.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parser.engine.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.net.URI;
import java.util.TimeZone;

@Slf4j
@Configuration
public class AwsConfig {

	@Value("${aws.s3.access_key:}")
	private String accessKey;

	@Value("${aws.s3.secret_key:}")
	private String secretKey;

	@Value("${aws.s3.region:ap-south-1}")
	private String region;

	@Bean
	@Profile(Constants.SpringProfile.PROD)
	public S3Client s3Client() {
		log.info("Creating s3 client with IAM role...");
		S3Configuration s3Configuration = S3Configuration.builder()
				.chunkedEncodingEnabled(true)
				.build();

		return S3Client.builder()
				.region(Region.of(region))
				.serviceConfiguration(s3Configuration)
				.build();
	}

	@Bean
	@Profile(Constants.SpringProfile.DEV)
	public S3Client localS3Client() {
		log.info("Creating local S3 client for development...");
		return S3Client.builder()
				.endpointOverride(URI.create("http://localhost:4566")) // LocalStack endpoint
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(accessKey, secretKey)))
				.forcePathStyle(true) // VERY IMPORTANT for LocalStack
				.build();
	}

	@Bean
	public SecretsManagerClient secretsManagerClient() {
		log.info("Creating secret manager client...");
		return SecretsManagerClient.builder()
				.region(Region.of(region))
				.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
				.region(Region.of(region))
				.build();
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
		return builder -> {
			builder.timeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			builder.modules(new JavaTimeModule());
		};
	}
}
