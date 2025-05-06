package com.parser.engine.service.impl;

import com.parser.engine.helper.ExcelHelper;
import com.parser.engine.service.FileService;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class FileServiceImpl implements FileService {

	private final ExcelHelper excelHelper;

	@Autowired
	public FileServiceImpl(ExcelHelper excelHelper) {
		this.excelHelper = excelHelper;
	}

	@Override
	public void processExcelFile(MultipartFile file, String fileId) {
		try {

		} catch(Exception e) {

		}

	}

}
