package com.parser.engine.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Slf4j
@Configuration
public class AwsConfig {

	@Value("${aws.s3.access_key}")
	private String accessKey;

	@Value("${aws.s3.secret_key}")
	private String secretKey;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Bean
	public S3Client s3Client() {

		S3Configuration s3Configuration = S3Configuration.builder()
				.chunkedEncodingEnabled(true)
				.build();

		StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

		return S3Client.builder()
				.region(Region.AP_SOUTH_1)
				.credentialsProvider(staticCredentialsProvider)
				.serviceConfiguration(s3Configuration)
				.build();
	}

	@Bean
	public SecretsManagerClient secretsManagerClient() {
		log.info("Creating secret manager client...");
		return SecretsManagerClient.builder()
				.region(Region.AF_SOUTH_1)
				.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
				.region(Region.AF_SOUTH_1)
				.build();
	}

}
