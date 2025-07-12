package com.parser.engine.controller;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.response.PropertyDetailsRespDto;
import com.parser.engine.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Page<PropertyDetailsRespDto>> searchProperty(
			@RequestParam(required = false) String numberOfBhk,
			@RequestParam(required = false) String numberOfRk,
			@RequestParam(required = false) String location,
			@RequestParam(required = false) String floor,
			@RequestParam(required = false) String furnishingStatus,
			@RequestParam(required = false) String area,
			@RequestParam(required = false) String quotedAmount,
			@RequestParam(required = false) String carParkingSlots,
			Pageable pageable) {
		log.info("Received request to search property with filter");
		PropertySearchFilterDto filter = PropertySearchFilterDto.builder()
				.numberOfBhk(numberOfBhk)
				.numberOfRk(numberOfRk)
				.location(location)
				.floor(floor)
				.furnishingStatus(furnishingStatus)
				.area(area)
				.quotedAmount(quotedAmount)
				.carParkingSlots(carParkingSlots)
				.build();
		return ResponseEntity.ok(propertyService.getPropertyDetails(filter, pageable));
	}
}
