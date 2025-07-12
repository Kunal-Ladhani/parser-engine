package com.parser.engine.spec;

import com.parser.engine.dto.filter.FileSearchFilterDto;
import com.parser.engine.entity.File;
import com.parser.engine.enums.FileProcessingStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
public class FileSpecification {

	public static Specification<File> withFilters(FileSearchFilterDto fileSearchFilterDto) {

		String fileName = fileSearchFilterDto.getFileName();
		String fileType = fileSearchFilterDto.getFileType();
		String uploadedBy = fileSearchFilterDto.getUploadedBy();
		FileProcessingStatus fileProcessingStatus = fileSearchFilterDto.getFileProcessingStatus();

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.hasText(fileName)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fileName")), "%" + fileName.toLowerCase() + "%"));
			}

			if (StringUtils.hasText(fileType)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("fileType")), "%" + fileType.toLowerCase() + "%"));
			}

			if (StringUtils.hasText(uploadedBy)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("uploadedBy")), "%" + uploadedBy.toLowerCase() + "%"));
			}

			if (Objects.nonNull(fileProcessingStatus)) {
				predicates.add(criteriaBuilder.equal(root.get("fileProcessingStatus"), fileProcessingStatus));
			}

			// Combine all predicates with AND
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
