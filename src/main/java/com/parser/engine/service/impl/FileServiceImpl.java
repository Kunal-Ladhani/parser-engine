package com.parser.engine.service.impl;

import com.parser.engine.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class FileServiceImpl implements FileService {

//	private final S3Client s3Client;
//
//	@Value("${aws.bucket-name}")
//	private String bucketName;
//
//	@Autowired
//	public FileServiceImpl(S3Client s3Client) {
//		this.s3Client = s3Client;
//	}
//
//	public String uploadFile(MultipartFile file) throws IOException {
//		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//
//		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//				.bucket(bucketName)
//				.key(fileName)
//				.contentType(file.getContentType())
//				.build();
//
//		s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
//
//		return fileName;
//	}

}
