package com.parser.engine.config;

import com.parser.engine.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Configuration
public class AwsConfig {

	@Value("${aws.s3.access_key}")
	private String accessKey;

	@Value("${aws.s3.secret_key}")
	private String secretKey;

	@Value("${aws.s3.region}")
	private String region;


	@Bean
	@Profile(Constants.SpringProfile.PROD)
	public S3Client s3Client() {
		log.info("Creating s3 client...");
		S3Configuration s3Configuration = S3Configuration.builder()
				.chunkedEncodingEnabled(true)
				.build();
		AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
		StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(awsBasicCredentials);

		return S3Client.builder()
				.region(Region.AP_SOUTH_1)
				.credentialsProvider(staticCredentialsProvider)
				.serviceConfiguration(s3Configuration)
				.build();
	}


	@Bean
	@Profile(Constants.SpringProfile.DEV)
	public S3Client localS3Client() {
		return S3Client.builder()
				.endpointOverride(URI.create("http://localhost:4566")) // LocalStack endpoint
				.region(Region.of(region))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(accessKey, secretKey)))
				.forcePathStyle(true) // VERY IMPORTANT for LocalStack
				.build();
	}

	@Bean
	@Profile(Constants.SpringProfile.PROD)
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
