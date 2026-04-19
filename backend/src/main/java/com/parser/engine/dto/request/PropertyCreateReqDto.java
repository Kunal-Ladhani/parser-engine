package com.parser.engine.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyCreateReqDto {

	// Basic property details
	private String buildingName;
	private String location;
	private String floor;
	private Double numberOfBhk;
	private Double numberOfRk;
	private FurnishingStatus furnishingStatus;
	private Double area;
	private Double quotedAmount;
	private Integer carParkingSlots;
	private String comment;

	// Broker details
	private String brokerName;
	private String brokerPhone;

	// Property type and status
	private ListingType listingType;
	private AvailabilityStatus availabilityStatus;

	// Date fields
	private LocalDateTime dateAdded;
	private LocalDateTime leaseEndDate;
	private LocalDateTime rentalEndDate;

	/**
	 * Check if required fields are provided for property creation
	 *
	 * @return true if all required fields are non-null
	 */
	public boolean hasRequiredFields() {
		return Objects.nonNull(buildingName)
				&& Objects.nonNull(location)
				&& Objects.nonNull(listingType)
				&& Objects.nonNull(availabilityStatus);
	}

	/**
	 * Get validation error message for missing required fields
	 *
	 * @return error message string
	 */
	public String getValidationErrorMessage() {
		if (Objects.isNull(buildingName)) {
			return "Building name is required";
		}
		if (Objects.isNull(location)) {
			return "Location is required";
		}
		if (Objects.isNull(listingType)) {
			return "Listing type is required";
		}
		if (Objects.isNull(availabilityStatus)) {
			return "Availability status is required";
		}
		return null;
	}
}
