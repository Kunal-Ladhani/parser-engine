package com.parser.engine.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for consistent date/time operations across the application. All
 * operations use India Standard Time (UTC+5:30)
 */
public class DateTimeUtils {

    public static final ZoneId INDIA_TIMEZONE = ZoneId.of("Asia/Kolkata");
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DISPLAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_FORMAT);

    /**
     * Get current date/time in India timezone
     */
    public static ZonedDateTime nowInIndia() {
        return ZonedDateTime.now(INDIA_TIMEZONE);
    }

    /**
     * Convert LocalDateTime to ZonedDateTime in India timezone
     */
    public static ZonedDateTime toIndiaTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(INDIA_TIMEZONE);
    }

    /**
     * Format date/time for display purposes
     */
    public static String formatForDisplay(ZonedDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Format date/time for API responses
     */
    public static String formatForApi(ZonedDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    /**
     * Get current date/time as formatted string for display
     */
    public static String nowAsDisplayString() {
        return formatForDisplay(nowInIndia());
    }

    /**
     * Get current date/time as formatted string for API
     */
    public static String nowAsApiString() {
        return formatForApi(nowInIndia());
    }
}
