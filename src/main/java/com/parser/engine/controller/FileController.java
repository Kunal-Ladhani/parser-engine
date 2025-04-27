package com.parser.engine.controller;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.exception.InvalidFileTypeException;
import com.parser.engine.helper.ExcelHelper;
import com.parser.engine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value = "/process-excel")
	public ResponseEntity<String> upload(@RequestParam MultipartFile file) {
		if (ExcelHelper.hasExcelFormat(file)) {
			String fileName = file.getOriginalFilename();
			log.info("file type = {}", file.getContentType());
			return ResponseEntity.ok("Uploaded: " + fileName);
		} else {
			throw new InvalidFileTypeException(ExceptionCode.F101, ExceptionCode.F101.getDefaultMessage() + file.getContentType());
		}
	}
}
