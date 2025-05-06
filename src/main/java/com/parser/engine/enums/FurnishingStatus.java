package com.parser.engine.enums;

import lombok.Getter;

@Getter
public enum FurnishingStatus {
	UNFURNISHED("Unfurnished"),
	SEMI_FURNISHED("Semi Furnished"),
	FULLY_FURNISHED("Fully Furnished");

	private final String label;

	FurnishingStatus(String label) {
		this.label = label;
	}
}