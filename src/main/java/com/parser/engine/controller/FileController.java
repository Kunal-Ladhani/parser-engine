package com.parser.engine.controller;

import com.parser.engine.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/aws/s3")
public class FileController {

	private final FileService fileService;

	@Autowired
	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping(value = "/process-excel", consumes = "application/vnd.ms-excel", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
		try {
			String fileName = file.getOriginalFilename();
			return ResponseEntity.ok("Uploaded: " + fileName);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
		}
	}

}
