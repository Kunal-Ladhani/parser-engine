package com.parser.engine.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyExcelDto {

	// 1RK/STUDIO
	@JsonFormat(pattern = "dd.MM.yy")
	@JsonProperty("Date added")
	private String dateAdded;

	@JsonProperty("Name of Building")
	private String buildingName;

	@JsonProperty("Location")
	private String location;

	@JsonProperty("Floor")
	private String floor;

	@JsonProperty("Furniture")
	private String furniture;

	@JsonProperty("Area")
	private String area;

	@JsonProperty("Car Park")
	private String carPark;

	@JsonProperty("Quoted")
	private String quoted;

	@JsonProperty("Comments")
	private String comment;

	@JsonProperty("Agent Name")
	private String brokerName;

	@JsonProperty("Agent Contact")
	private String brokerPhone;

	// L1BHK
	@JsonProperty("Amenities")
	private String amenities;

	// S1BHK
	@JsonProperty("Quoted in cr")
	private String quotedInCr;

	@JsonProperty("Area in carpet")
	private String areaInCarpet;

	// my added columns
	@JsonProperty("BHK")
	private String numberOfBhk;

	@JsonProperty("RK")
	private String numberOfRk;

	@JsonProperty("Availability")
	private String availabilityStatus;

	@JsonProperty("Listing Type")
	private String listingType;

	@JsonFormat(pattern = "dd.MM.yy")
	@JsonProperty("Lease End Date")
	private String leaseEndDate;

	@JsonFormat(pattern = "dd.MM.yy")
	@JsonProperty("Rental End Date")
	private String rentalEndDate;
	
}
