package com.parser.engine.enums;

import lombok.Getter;

@Getter
public enum FurnishingStatus {
	UF("Unfurnished"),
	SF("Semi Furnished"),
	FF("Fully Furnished");

	private final String label;

	FurnishingStatus(String label) {
		this.label = label;
	}
}