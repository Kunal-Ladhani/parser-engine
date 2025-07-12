package com.parser.engine.dao;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.dto.request.FileDetailsRespDto;
import com.parser.engine.entity.File;
import com.parser.engine.exception.ResourceDoesNotExistsException;
import com.parser.engine.mapper.FileMapper;
import com.parser.engine.repository.FileRepository;
import com.parser.engine.spec.FileSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class FileDao {

	private final FileRepository fileRepository;
	private final FileMapper fileMapper;

	@Autowired
	public FileDao(FileRepository fileRepository, FileMapper fileMapper) {
		this.fileRepository = fileRepository;
		this.fileMapper = fileMapper;
	}

	public File getFileMetadataById(UUID fileId) {
		return fileRepository.findById(fileId)
				.orElseThrow(() -> new ResourceDoesNotExistsException(ExceptionCode.F106, ExceptionCode.F106.getDefaultMessage() + fileId));
	}

	public Page<FileDetailsRespDto> getFileDetailsPageByFilter(FileSearchFilterDto fileSearchFilterDto, Pageable pageable) {
		log.info("Searching files with filter: {}", fileSearchFilterDto);

		// Create specification using criteria API
		Specification<File> spec = FileSpecification.withFilters(fileSearchFilterDto);

		// Execute query with specification
		Page<File> filePage = fileRepository.findAll(spec, pageable);
		log.info("Number of properties matching the filter: {}", filePage.getTotalElements());

		// Convert to DTO and return
		return filePage.hasContent() ? filePage.map(fileMapper::toResponseDto) : Page.empty(pageable);
	}

}
