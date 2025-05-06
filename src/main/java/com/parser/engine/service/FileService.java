package com.parser.engine.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	void processExcelFile(MultipartFile multipartFile, String fileId);

}
