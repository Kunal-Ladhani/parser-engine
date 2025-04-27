package com.parser.engine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

//@Configuration
public class AwsConfig {

//	@Value("${aws.access-key}")
//	private String accessKey;
//
//	@Value("${aws.secret-key}")
//	private String secretKey;
//
//	@Value("${aws.region}")
//	private String region;

//	@Value("${spring.profiles.active}")
//	private String activeProfile;

//	@Bean
//	public S3Client s3Client() {
//
//		S3Configuration s3Configuration = S3Configuration.builder()
//				.chunkedEncodingEnabled(true)
//				.build();
//
//		return S3Client.builder()
//				.region(Region.of(region))
//				.credentialsProvider(
//						StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
//				)
//				.serviceConfiguration(s3Configuration)
//				.build();
//	}

}
