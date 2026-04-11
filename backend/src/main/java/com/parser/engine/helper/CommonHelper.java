package com.parser.engine.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class CommonHelper {

	// Helper methods for parsing
	public static Double parseDouble(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			log.error("Invalid numeric value: {}", value);
			return null;
		}
	}

	public static Integer parseInt(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			log.error("Invalid integer value: {}", value);
			return null;
		}
	}
}
