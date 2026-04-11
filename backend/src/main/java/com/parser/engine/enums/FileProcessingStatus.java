package com.parser.engine.enums;

import lombok.Getter;

@Getter
public enum FileProcessingStatus {

	PENDING("Pending"),
	COMPLETED("Completed"),
	FAILED("Failed");

	private final String description;

	FileProcessingStatus(String description) {
		this.description = description;
	}

}