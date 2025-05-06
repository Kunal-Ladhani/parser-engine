package com.parser.engine.controller.wb;

import com.parser.engine.entity.File;
import com.parser.engine.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/wb/aws")
public class AwsController {

	private final AwsService awsService;

	@Autowired
	public AwsController(AwsService awsService) {
		this.awsService = awsService;
	}

	@PostMapping("/v1/upload")
	public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		File savedFile = awsService.uploadFileAndSave(file);
		return ResponseEntity.ok(savedFile);
	}

	@GetMapping("/v1/download/{key}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String key) {
		byte[] data = awsService.downloadFile(key);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(data);
	}

}
