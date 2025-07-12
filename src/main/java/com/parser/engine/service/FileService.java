package com.parser.engine.service;

import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.dto.request.FileDetailsRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FileService {

	void processExcelFile(UUID fileId);

	Page<FileDetailsRespDto> getFileDetails(FileSearchFilterDto fileSearchFilterDto, Pageable pageable);

}
