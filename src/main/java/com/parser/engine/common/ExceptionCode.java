package com.parser.engine.common;

import lombok.Getter;

public enum ExceptionCode {

	A101("A101", "Invalid login credentials"),
	A102("A102", "Invalid user credentials"),
	A103("A103", "Invalid User type"),
	A104("A104", "Authentication failure"),
	A105("A105", "Invalid Token"),
	A106("A106", "Token expired"),
	A107("A107", "User name not found"),
	A108("A108", "User name is disabled"),

	F101("F101", "Invalid file type"),
	F102("F102", "Invalid Upload type"),
	F103("F103", "Failed to parse this file"),

	N101("N101", "No field modified"),
	N102("N102", "No %s Input provided"),
	N103("N103", " %s should not be null"),

	O101("O101", "Operation not allowed"),

	S102("S102", "Service temporarily unavailable"),

	V101("V101", "Validation exception occurred"),
	V102("V102", "Invalid %s given"),
	V103("V103", "Creator Cannot Approve Request"),
	//V107("V107", "File processing failed."),

	U100("U100", "Unknown exception occurred"),
	U102("U102", "Nothing was modified"),

	R101("R101", "%s does not exist"),
	R102("R102", "%s already exist");

	@Getter
	private final String code;

	@Getter
	private final String defaultMessage;

	ExceptionCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
}

