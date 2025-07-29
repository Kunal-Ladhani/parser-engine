package com.parser.engine.common;

import lombok.Getter;

public enum ExceptionCode {

	// generic auth error
	A101("A101", "Authentication failed. Please check your credentials."),
	A102("A102", "Access denied. You don't have sufficient permission."),

	// jwt token errors
	A103("A103", "Token expired. Please login again."),
	A104("A104", "Invalid Token format. Please login again."),
	A105("A105", "Invalid token signature. Please login again."),
	A106("A106", "Unsupported token format. Please login again."),
	A107("A107", "Token processing failed. Please login again."),

	// Account related errors
	A108("A108", "Account is disabled. Please contact support."),
	A109("A109", "Account is locked. Please contact support."),
	A110("A110", "Account has expired. Please contact support."),

	//
	A111("A111", "Invalid username/email and password entered."),
	A112("A112", "Account not found. Please sign up first."),
	A113("A113", "Incorrect password. Please try again."),

	// Credential related errors
	A114("A114", "Username or Email is required."),
	A115("A115", "Username is already registered."),
	A116("A116", "Email is already registered."),

	// File related errors
	F101("F101", "Invalid file type"),
	F102("F102", "Invalid Upload type"),
	F103("F103", "Failed to parse this file"),
	F104("F104", "Failed to upload this file"),
	F105("F105", "Failed to download this file"),
	F106("F107", "File metadata not found for fileId: "),
	F107("F107", "Error while searching for files"),
	F108("F108", "File was deleted by: {}"),
	F109("F109", "File already processed by: {}"),

	// Properties related errors
	P101("P101", "Error while searching for properties"),
	P102("P102", "Property details not found for property Id: "),

	S102("S102", "Service temporarily unavailable"),

	// Validation related errors
	V101("V101", "Validation exception occurred"),
	V102("V102", "Invalid %s given"),
	V103("V103", "Creator Cannot Approve Request"),

	// Generic unknown errors
	U100("U100", "Unknown exception occurred."),
	U102("U102", "Nothing was modified."),

	// Resource related errors
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
