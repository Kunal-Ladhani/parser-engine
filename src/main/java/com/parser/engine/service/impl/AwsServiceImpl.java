package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.entity.File;
import com.parser.engine.exception.AwsS3Exception;
import com.parser.engine.helper.AwsHelper;
import com.parser.engine.repository.FileRepository;
import com.parser.engine.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class AwsServiceImpl implements AwsService {

	private final AwsHelper awsHelper;
	private final FileRepository fileRepository;

	@Autowired
	public AwsServiceImpl(AwsHelper awsHelper,
						  FileRepository fileRepository) {
		this.awsHelper = awsHelper;
		this.fileRepository = fileRepository;

	}

	@Override
	public File uploadFileAndSave(MultipartFile multipartFile) {
		try {
			String awsKey = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
			String fileName = multipartFile.getOriginalFilename();
			String contentType = multipartFile.getContentType();
			InputStream inputStream = multipartFile.getInputStream();
			File file = awsHelper.postToS3(fileName, awsKey, contentType, inputStream);
			return fileRepository.save(file);
		} catch (Exception e) {
			log.error("Error while uploading and saving file to S3: {}", e.getMessage());
			throw new AwsS3Exception(ExceptionCode.F104, ExceptionCode.F104.getDefaultMessage());
		}
	}

	@Override
	public byte[] downloadFile(String key) {
		try {
			return awsHelper.getFromS3(key);
		} catch (Exception e) {
			log.error("Error while downloading file from S3: {}", e.getMessage());
			throw new AwsS3Exception(ExceptionCode.F105, ExceptionCode.F105.getDefaultMessage());
		}
	}

}
