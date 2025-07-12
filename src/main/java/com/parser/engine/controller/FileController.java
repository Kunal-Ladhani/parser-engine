package com.parser.engine.controller;

import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.dto.request.FileDetailsRespDto;
import com.parser.engine.enums.FileProcessingStatus;
import com.parser.engine.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/wb/v1/files")
public class FileController {

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@GetMapping
	public ResponseEntity<Page<FileDetailsRespDto>> searchFiles(
			@RequestParam(required = false) String fileName,
			@RequestParam(required = false) String fileType,
			@RequestParam(required = false) FileProcessingStatus fileProcessingStatus,
			@RequestParam(required = false) String uploadedBy,
			Pageable pageable) {
		log.info("Received request to search files with filter");
		FileSearchFilterDto filter = FileSearchFilterDto.builder()
				.fileName(fileName)
				.fileType(fileType)
				.fileProcessingStatus(fileProcessingStatus)
				.uploadedBy(uploadedBy)
				.build();
		return ResponseEntity.ok(fileService.getFileDetails(filter, pageable));
	}

	@PostMapping(value = "/{fileId}/process")
	public ResponseEntity<String> processFile(@PathVariable UUID fileId) {
		fileService.processExcelFile(fileId);
		return ResponseEntity.ok("Processed.");
	}


}
