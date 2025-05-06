package com.parser.engine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable implements Serializable {

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	private String createdBy;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private ZonedDateTime createdAt;

	@LastModifiedBy
	@Column(name = "last_updated_by")
	private String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_updated_at")
	private ZonedDateTime lastModifiedAt;

}