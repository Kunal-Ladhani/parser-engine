package com.parser.engine.dao;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.entity.File;
import com.parser.engine.exception.ResourceDoesNotExistsException;
import com.parser.engine.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FileDao {

	private final FileRepository fileRepository;

	@Autowired
	public FileDao(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	public File getFileMetadataById(UUID fileId) {
		return fileRepository.findById(fileId)
				.orElseThrow(() -> new ResourceDoesNotExistsException(ExceptionCode.F106, ExceptionCode.F106.getDefaultMessage() + fileId));
	}

}
