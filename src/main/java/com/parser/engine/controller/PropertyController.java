package com.parser.engine.controller;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.request.PropertyDetailsUpdateReqDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequestMapping("/wb/v1/properties")
@RestController
public class PropertyController {

	private final PropertyService propertyService;

	@Autowired
	public PropertyController(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	@GetMapping
	public ResponseEntity<Page<PropertySearchRespDto>> searchProperty(
			@RequestParam(required = false) String buildingName,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) String numberOfBhk,
			@RequestParam(required = false) String numberOfRk,

			@RequestParam(required = false) String area,
			@RequestParam(required = false) String furnishingStatus,
			@RequestParam(required = false) String floor,
			@RequestParam(required = false) String carParkingSlots,

			@RequestParam(required = false) String quotedAmount,
			@RequestParam(required = false) String listingType,
			@RequestParam(required = false) String availabilityStatus,

			Pageable pageable) {
		log.info("Received request to search property with filter");
		PropertySearchFilterDto filter = PropertySearchFilterDto.builder()
				.buildingName(buildingName)
				.location(location)
				.numberOfBhk(numberOfBhk)
				.numberOfRk(numberOfRk)
				.area(area)
				.furnishingStatus(furnishingStatus)
				.floor(floor)
				.carParkingSlots(carParkingSlots)
				.quotedAmount(quotedAmount)
				.listingType(listingType)
				.availabilityStatus(availabilityStatus)
				.build();
		return ResponseEntity.ok(propertyService.fetchPropertyList(filter, pageable));
	}

	@GetMapping("/{propertyId}")
	public ResponseEntity<PropertyDetailRespDto> getPropertyDetails(@PathVariable UUID propertyId) {
		log.info("Received request to get details for property with id: {}", propertyId);
		return ResponseEntity.ok(propertyService.fetchPropertyDetail(propertyId));
	}

	@PatchMapping("/{propertyId}")
	public ResponseEntity<PropertyDetailRespDto> updatePropertyDetails(@PathVariable UUID propertyId, @RequestBody PropertyDetailsUpdateReqDto propertyDetailsUpdateReqDto) {
		log.info("Received request to update details for property with id: {}", propertyId);
		PropertyDetailRespDto updatedProperty = propertyService.updatePropertyDetail(propertyId, propertyDetailsUpdateReqDto);
		return ResponseEntity.ok(updatedProperty);
	}

}
