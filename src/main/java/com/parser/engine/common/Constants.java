package com.parser.engine.common;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class Constants {

	public static final Set<String> EXCEL_SUPPORTED_TYPES = Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel");
	public static final String UPLOAD_CONTENT = "Upload Content";

	public static class SpringProfile {
		public static final String DEV = "dev";
		public static final String UAT = "uat";
		public static final String BETA = "beta";
		public static final String PROD = "prod";

		private SpringProfile() {
		}
	}

	public static class HttpHeaders {
		private HttpHeaders() {
		}
	}
}
