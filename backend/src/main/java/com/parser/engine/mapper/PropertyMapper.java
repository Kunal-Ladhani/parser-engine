package com.parser.engine.mapper;

import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring", imports = {Double.class, Integer.class, LocalDateTime.class})
public interface PropertyMapper {

	//	@Mapping(target = "brokerName", source = "agent", qualifiedByName = "extractBrokerName")
	//	@Mapping(target = "brokerPhone", source = "agent", qualifiedByName = "extractBrokerPhone")
	@Mapping(target = "area", expression = "java(parseDouble(dto.getArea()))")
	@Mapping(target = "quotedAmount", expression = "java(parseDouble(dto.getQuoted()))")
	@Mapping(target = "carParkingSlots", expression = "java(parseInteger(dto.getCarPark()))")
	@Mapping(target = "numberOfBhk", expression = "java(parseDouble(dto.getNumberOfBhk()))")
	@Mapping(target = "numberOfRk", expression = "java(parseDouble(dto.getNumberOfRk()))")
	@Mapping(target = "furnishingStatus", expression = "java(parseFurnishingStatus(dto.getFurniture()))")
	@Mapping(target = "availabilityStatus", expression = "java(parseAvailabilityStatus(dto.getAvailabilityStatus()))")
	@Mapping(target = "listingType", expression = "java(parseListingType(dto.getListingType()))")
	@Mapping(target = "dateAdded", expression = "java(parseDateTime(dto.getDateAdded()))")
	@Mapping(target = "leaseEndDate", expression = "java(parseDateTime(dto.getLeaseEndDate()))")
	@Mapping(target = "rentalEndDate", expression = "java(parseDateTime(dto.getRentalEndDate()))")
	@Mapping(target = "brokerPhone", expression = "java(formatPhoneNumber(dto.getBrokerPhone()))")
	Property toEntity(PropertyExcelDto dto);

	PropertySearchRespDto toSearchResponseDto(Property entity);

	// -------------------------- COMMON HELPER ---------------------------

	default Double parseDouble(String value) {
		if (value == null || value.trim().isEmpty())
			return null;

		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	default Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty())
			return null;

		try {
			// Handle cases where the value might have decimals (like "1.0")
			double doubleValue = Double.parseDouble(value.trim());
			return (int) doubleValue;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// -------------------------- DATE TIME HELPER ---------------------------

	default LocalDateTime parseDateTime(String value) {
		if (value == null || value.trim().isEmpty())
			return null;

		try {
			// Excel date format is always dd.MM.yyyy
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
			String trimmedValue = value.trim();

			// Parse as LocalDate first, then convert to LocalDateTime at start of day
			LocalDate date = LocalDate.parse(trimmedValue, formatter);
			return date.atStartOfDay();
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	// -------------------------- PHONE NUMBER HELPER ---------------------------

	default String formatPhoneNumber(String value) {
		if (value == null || value.trim().isEmpty())
			return null;

		try {
			String trimmedValue = value.trim();

			// Handle scientific notation (e.g., "9.322296674E9")
			if (trimmedValue.contains("E") || trimmedValue.contains("e")) {
				double phoneAsDouble = Double.parseDouble(trimmedValue);
				// Convert to long to remove decimal places, then to string
				long phoneAsLong = (long) phoneAsDouble;
				return String.valueOf(phoneAsLong);
			}

			// Handle regular decimal numbers (e.g., "9322296674.0")
			if (trimmedValue.contains(".")) {
				double phoneAsDouble = Double.parseDouble(trimmedValue);
				long phoneAsLong = (long) phoneAsDouble;
				return String.valueOf(phoneAsLong);
			}

			// Return as is if it's already a regular number string
			return trimmedValue;
		} catch (NumberFormatException e) {
			// If it's not a number, return as is
			return value.trim();
		}
	}

	// -------------------------- ENUM HELPER ---------------------------

	default FurnishingStatus parseFurnishingStatus(String value) {
		if (value == null || value.trim().isEmpty())
			return FurnishingStatus.UF;

		try {
			return FurnishingStatus.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	default AvailabilityStatus parseAvailabilityStatus(String value) {
		if (value == null || value.trim().isEmpty())
			return AvailabilityStatus.AVAILABLE;

		try {
			return AvailabilityStatus.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	default ListingType parseListingType(String value) {
		if (value == null || value.trim().isEmpty())
			return ListingType.RENT;

		try {
			return ListingType.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	// -------------------------- VALUE EXTRACTOR ---------------------------

	@Named("extractBrokerPhone")
	default String extractBrokerPhone(String agent) {
		if (agent == null || agent.trim().isEmpty())
			return null;

		// Use regex to find 10-digit phone number
		Pattern pattern = Pattern.compile("\\b\\d{10}\\b");
		Matcher matcher = pattern.matcher(agent);

		if (matcher.find()) {
			return matcher.group();
		}

		return null;
	}

	@Named("extractBrokerName")
	default String extractBrokerName(String agent) {
		if (agent == null || agent.trim().isEmpty())
			return null;

		// Remove the phone number to get the name
		String phoneNumber = extractBrokerPhone(agent);
		if (phoneNumber != null) {
			// Remove the phone number and clean up extra spaces
			String name = agent.replace(phoneNumber, "").trim();
			// Remove any extra spaces that might be left
			name = name.replaceAll("\\s+", " ");
			return name.isEmpty() ? null : name;
		}

		// If no phone number found, return the original string trimmed
		return agent.trim();
	}

}
