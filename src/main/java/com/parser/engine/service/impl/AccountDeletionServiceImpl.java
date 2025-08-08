package com.parser.engine.service.impl;

import com.parser.engine.entity.File;
import com.parser.engine.entity.Property;
import com.parser.engine.entity.User;
import com.parser.engine.repository.FileRepository;
import com.parser.engine.repository.PropertyRepository;
import com.parser.engine.repository.RefreshTokenRepository;
import com.parser.engine.service.AccountDeletionService;
import com.parser.engine.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import com.parser.engine.utils.DateTimeUtils;
import java.util.List;

@Slf4j
@Service
public class AccountDeletionServiceImpl implements AccountDeletionService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final FileRepository fileRepository;
	private final PropertyRepository propertyRepository;

	@Autowired
	public AccountDeletionServiceImpl(
			RefreshTokenRepository refreshTokenRepository,
			FileRepository fileRepository,
			PropertyRepository propertyRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.fileRepository = fileRepository;
		this.propertyRepository = propertyRepository;
	}

	@Override
	@Transactional
	public int deleteUserRefreshTokens(User user) {
		int deletedCount = refreshTokenRepository.deleteByUser(user);
		log.info("Deleted {} refresh tokens for user: {}", deletedCount, user.getEmail());
		return deletedCount;
	}

	@Override
	@Transactional
	public int deleteUserFiles(User user) {
		// Find files created by this user (checking Auditable fields)
		List<File> userFiles = fileRepository.findByCreatedByOrUploadedBy(user.getEmail(), user.getEmail());

		// Mark as deleted (soft delete)
		userFiles.forEach(file -> {
			file.setIsDeleted(true);
			file.setDeletedAt(DateTimeUtils.nowInIndia());
			file.setDeletedBy(SecurityUtils.getLoggedInUserEmail());
		});

		fileRepository.saveAll(userFiles);
		log.info("Soft deleted {} files for user: {}", userFiles.size(), user.getEmail());
		return userFiles.size();
	}

	@Override
	@Transactional
	public int anonymizeUserFiles(User user) {
		List<File> userFiles = fileRepository.findByCreatedByOrUploadedBy(
				user.getEmail(), user.getEmail()
		);

		// Anonymize user references
		userFiles.forEach(file -> {
			if (user.getEmail().equals(file.getCreatedBy())) {
				file.setCreatedBy("DELETED_USER");
			}
			if (user.getEmail().equals(file.getUploadedBy())) {
				file.setUploadedBy("DELETED_USER");
			}
			if (user.getEmail().equals(file.getLastModifiedBy())) {
				file.setLastModifiedBy("DELETED_USER");
			}
		});

		fileRepository.saveAll(userFiles);
		log.info("Anonymized {} files for user: {}", userFiles.size(), user.getEmail());
		return userFiles.size();
	}

	@Override
	@Transactional
	public int deleteUserProperties(User user) {
		List<Property> userProperties = propertyRepository.findByCreatedBy(user.getEmail());

		// Hard delete properties (or mark as deleted if you have soft delete fields)
		propertyRepository.deleteAll(userProperties);
		log.info("Deleted {} properties for user: {}", userProperties.size(), user.getEmail());
		return userProperties.size();
	}

	@Override
	@Transactional
	public int anonymizeUserProperties(User user) {
		List<Property> userProperties = propertyRepository.findByCreatedBy(user.getEmail());

		// Anonymize user references in properties
		userProperties.forEach(property -> {
			if (user.getEmail().equals(property.getCreatedBy())) {
				property.setCreatedBy("DELETED_USER");
			}
			if (user.getEmail().equals(property.getLastModifiedBy())) {
				property.setLastModifiedBy("DELETED_USER");
			}
		});

		propertyRepository.saveAll(userProperties);
		log.info("Anonymized {} properties for user: {}", userProperties.size(), user.getEmail());
		return userProperties.size();
	}
}
