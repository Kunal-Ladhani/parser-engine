package com.parser.engine.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dao.FileDao;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.entity.File;
import com.parser.engine.exception.InvalidFileTypeException;
import com.parser.engine.exception.ServiceException;
import com.parser.engine.helper.AwsHelper;
import com.parser.engine.helper.ExcelHelper;
import com.parser.engine.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final AwsHelper awsHelper;
    private final ExcelHelper excelHelper;
    private final FileDao fileDao;

    @Autowired
    public FileServiceImpl(AwsHelper awsHelper, ExcelHelper excelHelper, FileDao fileDao) {
        this.awsHelper = awsHelper;
        this.excelHelper = excelHelper;
        this.fileDao = fileDao;
    }

    @Override
    public void processExcelFile(UUID fileId) {

        log.info("Starting file processing for fileId: {}", fileId);
        File file = fileDao.getFileMetadataById(fileId);
        if (!excelHelper.hasExcelFormat(file.getContentType())) {
            throw new InvalidFileTypeException(ExceptionCode.F101, ExceptionCode.F101.getDefaultMessage());
        }

        try {
            InputStreamResource resource = awsHelper.getFromS3(file.getAwsKey());
            List<PropertyExcelDto> rawData = excelHelper.extractDataFromExcel(resource.getInputStream());

        } catch (Exception e) {
            log.error("Error occurred while processing excel file with fileId: {}", fileId);
            throw new ServiceException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage());
        }

    }

}
