package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.entity.File;
import com.parser.engine.exception.AwsS3Exception;
import com.parser.engine.exception.ResourceAlreadyExistsException;
import com.parser.engine.helper.AwsHelper;
import com.parser.engine.repository.FileRepository;
import com.parser.engine.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	public InputStreamResource downloadFile(File file) {
		try {
			return awsHelper.getFromS3(file.getAwsKey());
		} catch (Exception e) {
			log.error("Error while downloading file from S3: {}", e.getMessage());
			throw new AwsS3Exception(ExceptionCode.F105, ExceptionCode.F105.getDefaultMessage());
		}
	}

	@Override
	public File uploadFileAndSave(MultipartFile multipartFile) {
		try {
			// Extract file name (without extension) for duplicate check
			String originalFileName = multipartFile.getOriginalFilename();
			if (originalFileName == null || originalFileName.trim().isEmpty()) {
				throw new AwsS3Exception(ExceptionCode.F101, "File name cannot be empty");
			}

			String fileNameWithoutExtension = FilenameUtils.getBaseName(originalFileName);
			log.info("Checking for duplicate file name: {}", fileNameWithoutExtension);

			// Check if file name already exists (excluding deleted files)
			if (fileRepository.existsByFileNameAndIsDeletedFalse(fileNameWithoutExtension)) {
				log.error("File with name '{}' already exists", fileNameWithoutExtension);
				throw new ResourceAlreadyExistsException(ExceptionCode.F110, ExceptionCode.F110.getDefaultMessage());
			}

			// Proceed with upload if no duplicate found
			File file = awsHelper.postToS3(multipartFile);
			return fileRepository.save(file);
		} catch (ResourceAlreadyExistsException e) {
			log.error("Error occurred while uploading and saving file to S3: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error while uploading and saving file to S3: {}", e.getMessage());
			throw new AwsS3Exception(ExceptionCode.F104, ExceptionCode.F104.getDefaultMessage());
		}
	}

}
