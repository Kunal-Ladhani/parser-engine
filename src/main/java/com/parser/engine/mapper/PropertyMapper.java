package com.parser.engine.mapper;

import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.response.PropertyDetailsRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.enums.FurnishingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mapper(componentModel = "spring", imports = {Double.class, Integer.class, LocalDateTime.class})
public interface PropertyMapper {

	PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

	@Mapping(target = "buildingName", source = "buildingName")
	@Mapping(target = "floor", source = "floor")
	@Mapping(target = "location", source = "location")
	@Mapping(target = "area", expression = "java(parseDouble(dto.getArea()))")
	@Mapping(target = "quotedAmount", expression = "java(parseDouble(dto.getQuoted()))")
	@Mapping(target = "carParkingSlots", expression = "java(parseInteger(dto.getCarPark()))")
	@Mapping(target = "furnishingStatus", expression = "java(parseFurnishingStatus(dto.getFurniture()))")
	@Mapping(target = "comment", source = "comment")
	@Mapping(target = "brokerName", source = "agent", qualifiedByName = "extractBrokerName") // separate agent name and number - is it a list or single ?
	@Mapping(target = "brokerPhone", source = "agent", qualifiedByName = "extractBrokerPhone") // separate agent name and number - is it a list or single ?
	@Mapping(target = "availabilityStatus", constant = "AVAILABLE")
	@Mapping(target = "listingType", ignore = true)    // populate it - is it for rent, lease or sale
	@Mapping(target = "leaseOrRentExpiryDate", ignore = true)
	@Mapping(target = "numberOfBhk", ignore = true)  // populate it
	@Mapping(target = "numberOfRk", ignore = true)	// populate it
	Property toEntity(PropertyExcelDto dto);

	PropertyDetailsRespDto toResponseDto(Property entity);

	// custom parsers
	default Double parseDouble(String value) {
		if (value == null || value.trim().isEmpty()) return null;
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	default Integer parseInteger(String value) {
		if (value == null || value.trim().isEmpty()) return null;
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	default FurnishingStatus parseFurnishingStatus(String value) {
		if (value == null) return null;
		try {
			return FurnishingStatus.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

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