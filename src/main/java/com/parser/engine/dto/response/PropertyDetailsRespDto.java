package com.parser.engine.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDetailsRespDto {

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

	private String brokerName;

	private String brokerPhone;

	private ListingType listingType;

	private AvailabilityStatus availabilityStatus;

	private ZonedDateTime leaseOrRentExpiryDate;

}
