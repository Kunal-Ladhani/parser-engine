package com.parser.engine.controller;

import com.parser.engine.dao.FileDao;
import com.parser.engine.entity.File;
import com.parser.engine.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/wb/aws")
public class AwsController {

	private final AwsService awsService;
	private final FileDao fileDao;

	@Autowired
	public AwsController(AwsService awsService, FileDao fileDao) {
		this.awsService = awsService;
		this.fileDao = fileDao;
	}

	@PostMapping("/v1/upload")
	public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		File savedFile = awsService.uploadFileAndSave(file);
		return ResponseEntity.ok(savedFile);
	}

	@GetMapping("/v1/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) {
		File metadata = fileDao.getFileMetadataById(fileId);
		InputStreamResource resource = awsService.downloadFile(metadata);
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(metadata.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getFileName() + "\"")
				.header(HttpHeaders.ETAG, metadata.getEtag())
				.body(resource);
	}

}
