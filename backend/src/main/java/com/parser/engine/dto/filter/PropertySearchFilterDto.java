package com.parser.engine.dto.filter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PropertySearchFilterDto {

	private String buildingName;
	private String location;
	private String numberOfBhk;
	private String numberOfRk;

	private String area;
	private String furnishingStatus;
	private String floor;
	private String carParkingSlots;

	private String quotedAmount;
	private String listingType;
	private String availabilityStatus;

//    public boolean isAtleastOneFilterPresent() {
//        String builder = numberOfRk
//                + numberOfBhk
//                + location
//                + floor
//                + furnishingStatus
//                + area
//                + quotedAmount
//                + carParkingSlots;
//        return StringUtils.hasText(builder
//                .replace("null", "")
//                .replace("\\[\\]", ""));
//    }
}
