package com.parser.engine.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Custom Jackson deserializer for LocalDateTime to handle multiple date formats
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter PRIMARY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter SECONDARY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter TERTIARY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateTimeString = p.getText();

        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }

        // Try different formats
        try {
            return LocalDateTime.parse(dateTimeString, PRIMARY_FORMATTER);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDateTime.parse(dateTimeString, SECONDARY_FORMATTER);
            } catch (DateTimeParseException e2) {
                try {
                    return LocalDateTime.parse(dateTimeString, TERTIARY_FORMATTER);
                } catch (DateTimeParseException e3) {
                    throw new IOException("Unable to parse date: " + dateTimeString, e3);
                }
            }
        }
    }
}
