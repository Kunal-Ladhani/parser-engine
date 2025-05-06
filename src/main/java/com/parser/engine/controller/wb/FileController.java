package com.parser.engine.controller.wb;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.exception.InvalidFileTypeException;
import com.parser.engine.helper.ExcelHelper;
import com.parser.engine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/wb/files")
public class FileController {

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value = "{fileId}/process")
	public ResponseEntity<String> processFile(@PathVariable String fileId, @RequestParam MultipartFile file) {
		if (ExcelHelper.hasExcelFormat(file)) {
			fileService.processExcelFile(file, fileId);
			return ResponseEntity.ok("Uploaded.");
		} else {
			throw new InvalidFileTypeException(ExceptionCode.F101, ExceptionCode.F101.getDefaultMessage() + file.getContentType());
		}
	}
}
