package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dao.FileDao;
import com.parser.engine.dao.PropertyDao;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.dto.response.FileDetailsRespDto;
import com.parser.engine.entity.File;
import com.parser.engine.exception.InvalidFileTypeException;
import com.parser.engine.exception.ServiceException;
import com.parser.engine.helper.AwsHelper;
import com.parser.engine.helper.ExcelHelper;
import com.parser.engine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

	private final AwsHelper awsHelper;
	private final ExcelHelper excelHelper;
	private final FileDao fileDao;
	private final PropertyDao propertyDao;

	@Autowired
	public FileServiceImpl(AwsHelper awsHelper, ExcelHelper excelHelper, FileDao fileDao, PropertyDao propertyDao) {
		this.awsHelper = awsHelper;
		this.excelHelper = excelHelper;
		this.fileDao = fileDao;
		this.propertyDao = propertyDao;
	}

	@Override
	public void processExcelFile(UUID fileId) {

		log.info("Starting file processing for fileId: {}", fileId);
		// find the fileId in metadata db
		File file = fileDao.getFileMetadataById(fileId);
		log.info("file data: {}", file);

		// check if it actually has Excel format
		if (!excelHelper.hasExcelFormat(file.getContentType())) {
			throw new InvalidFileTypeException(ExceptionCode.F101, ExceptionCode.F101.getDefaultMessage());
		}

		try {
			InputStreamResource resource = awsHelper.getFromS3(file.getAwsKey());
			var inputStream = resource.getInputStream();

			// position-based
			// List<PropertyExcelDto> rawData = excelHelper.extractDataFromExcel(inputStream);

			// header-based
			List<PropertyExcelDto> propertyExcelDtoList = excelHelper.extractDataFromExcelByHeaders(inputStream);
			log.info("extracted by headers data: {}", propertyExcelDtoList);

			// mapstruct-based
			// List<PropertyExcelDto> excelData = excelHelper.extractDataFromExcelWithMapStruct(inputStream);

			propertyDao.savePropertyData(propertyExcelDtoList);
		} catch (Exception e) {
			log.error("Error occurred while processing excel file with fileId: {}", fileId);
			throw new ServiceException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage());
		}

	}

	@Override
	public Page<FileDetailsRespDto> getFileDetails(FileSearchFilterDto fileSearchFilterDto, Pageable pageable) {
		try {
			log.info("Fetching file details with the filter: {}", fileSearchFilterDto.toString());

			Page<FileDetailsRespDto> fileDetailsRespDtoPage = fileDao.getFileDetailsPageByFilter(fileSearchFilterDto, pageable);
			log.info("File details by filter: {}", fileDetailsRespDtoPage.getContent());

			return fileDetailsRespDtoPage;
		} catch (Exception e) {
			log.error("Error occurred while searching for files: {}", e.getMessage());
			throw new ServiceException(ExceptionCode.F107, ExceptionCode.F107.getDefaultMessage());
		}
	}

}
