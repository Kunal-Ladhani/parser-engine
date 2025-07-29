package com.parser.engine.repository;

import com.parser.engine.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID>, JpaSpecificationExecutor<File> {

	List<File> findByCreatedByOrUploadedBy(String createdBy, String uploadedBy);

	List<File> findByCreatedBy(String createdBy);
}
