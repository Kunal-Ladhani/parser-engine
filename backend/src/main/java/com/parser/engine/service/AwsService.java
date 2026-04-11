package com.parser.engine.service;

import com.parser.engine.entity.File;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AwsService {

	File uploadFileAndSave(MultipartFile file) throws IOException;

	InputStreamResource downloadFile(File file);

}
