package com.parser.engine.dao;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.dto.response.FileDetailsRespDto;
import com.parser.engine.entity.File;
import com.parser.engine.enums.FileProcessingStatus;
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

import java.time.ZonedDateTime;
import java.util.Objects;
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

	public void markFileStatus(UUID fileId, FileProcessingStatus status) {
		try {
			File file = this.getFileMetadataById(fileId);
			if (Objects.nonNull(file)) {
				if (FileProcessingStatus.COMPLETED.equals(status)) {
					file.setIsProcessed(true);
					file.setProcessedAt(ZonedDateTime.now());
					file.setProcessedBy("kunalladhani@gmail.com");    // TODO: use `SecurityUtils.getCurrentLoggedInUser()`
				}
				file.setFileProcessingStatus(status);
				fileRepository.save(file);
			}

		} catch (Exception e) {
			log.error("Error occurred while marking file status for fileId: {}", fileId, e);
		}
	}

	public void softDeleteFile(UUID fileId) {
		try {
			File file = this.getFileMetadataById(fileId);
			if (Objects.nonNull(file) && !file.getIsDeleted()) {
				file.setIsDeleted(true);
				file.setDeletedAt(ZonedDateTime.now());
				file.setDeletedBy("kunalladhani@gmail.com");    // TODO: use `SecurityUtils.getCurrentLoggedInUser()`
				fileRepository.save(file);
			}
		} catch (Exception e) {
			log.error("Error occurred while marking file DELETED for fileId: {}", fileId, e);
		}
	}

}
