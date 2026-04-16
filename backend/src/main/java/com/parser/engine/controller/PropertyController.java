package com.parser.engine.controller;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.request.PropertyCreateReqDto;
import com.parser.engine.dto.request.PropertyDetailsUpdateReqDto;
import com.parser.engine.dto.request.PropertyStatusUpdateReqDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/wb/v1/properties")
@RestController
public class PropertyController {

	private final PropertyService propertyService;

	@PostMapping
	public ResponseEntity<PropertyDetailRespDto> createProperty(@RequestBody PropertyCreateReqDto propertyCreateReqDto) {
		log.info("Received request to create new property with data: {}", propertyCreateReqDto);
		PropertyDetailRespDto createdProperty = propertyService.createProperty(propertyCreateReqDto);
		return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
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
		log.info("Received request to get details for property id: {}", propertyId);
		return ResponseEntity.ok(propertyService.fetchPropertyDetail(propertyId));
	}

	@PatchMapping("/{propertyId}")
	public ResponseEntity<PropertyDetailRespDto> updatePropertyDetails(@PathVariable UUID propertyId, @RequestBody PropertyDetailsUpdateReqDto propertyDetailsUpdateReqDto) {
		log.info("Received request to update details for property id: {}", propertyId);
		PropertyDetailRespDto updatedProperty = propertyService.updatePropertyDetail(propertyId, propertyDetailsUpdateReqDto);
		return ResponseEntity.ok(updatedProperty);
	}

	@PatchMapping("/{propertyId}/mark_status")
	public ResponseEntity<PropertyDetailRespDto> updatePropertyStatus(@PathVariable UUID propertyId, @RequestBody PropertyStatusUpdateReqDto propertyStatusUpdateReqDto) {
		log.info("Received request to update status for property id: {}", propertyId);
		PropertyDetailRespDto updatedProperty = propertyService.updatePropertyStatus(propertyId, propertyStatusUpdateReqDto);
		return ResponseEntity.ok(updatedProperty);
	}

}
