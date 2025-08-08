package com.parser.engine.config;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

import com.parser.engine.utils.DateTimeUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA Converter to handle ZonedDateTime persistence in India timezone
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        // Convert to India timezone if not already, then to Timestamp for DB storage
        ZonedDateTime indiaTime = zonedDateTime.withZoneSameInstant(DateTimeUtils.INDIA_TIMEZONE);
        return Timestamp.from(indiaTime.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        // Convert from UTC timestamp back to India timezone
        return timestamp.toInstant().atZone(DateTimeUtils.INDIA_TIMEZONE);
    }
}
