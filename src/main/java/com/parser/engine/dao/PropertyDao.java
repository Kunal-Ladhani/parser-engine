package com.parser.engine.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.request.PropertyDetailsUpdateReqDto;
import com.parser.engine.dto.request.PropertyStatusUpdateReqDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.exception.ResourceDoesNotExistsException;
import com.parser.engine.exception.ValidationException;
import com.parser.engine.mapper.PropertyMapper;
import com.parser.engine.repository.PropertyRepository;
import com.parser.engine.spec.PropertySpecification;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertyDao {

	private final ModelMapper modelMapper;
	private final PropertyMapper propertyMapper;
	private final PropertyRepository propertyRepository;

	@Autowired
	public PropertyDao(ModelMapper modelMapper,
					   PropertyMapper propertyMapper,
					   PropertyRepository propertyRepository) {
		this.modelMapper = modelMapper;
		this.propertyMapper = propertyMapper;
		this.propertyRepository = propertyRepository;
	}

	public void savePropertyData(List<PropertyExcelDto> propertyExcelDtoList) {
		log.info("Saving property excel dto list.");
		List<Property> entities = propertyExcelDtoList.stream()
				.map(propertyMapper::toEntity)
				.collect(Collectors.toList());
		log.info("Number of property entities: {}", entities.size());
		propertyRepository.saveAll(entities);
	}

	public Page<PropertySearchRespDto> getPropertyDetailsPageByFilter(PropertySearchFilterDto propertySearchFilterDto, Pageable pageable) {
		log.info("Searching properties with filter: {}", propertySearchFilterDto);

		// Create specification using criteria API
		Specification<Property> spec = PropertySpecification.withFilters(propertySearchFilterDto);

		// Execute the query with specification
		Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);
		log.info("Number of properties matching the filter: {}", propertyPage.getTotalElements());

		// Convert to DTO and return
		return propertyPage.hasContent() ? propertyPage.map(propertyMapper::toSearchResponseDto) : Page.empty(pageable);
	}

	public Property getPropertyById(UUID propertyId) {
		log.info("Fetching property with id: {}", propertyId);
		return propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceDoesNotExistsException(ExceptionCode.P102, ExceptionCode.P102.getDefaultMessage() + propertyId));
	}

	public PropertyDetailRespDto getPropertyDetailById(UUID propertyId) {
		log.info("Fetching details for property with id: {}", propertyId);
		Property property = this.getPropertyById(propertyId);
		return Objects.nonNull(property) ? modelMapper.map(property, PropertyDetailRespDto.class) : null;
	}

	@Transactional
	public PropertyDetailRespDto updateProperty(UUID propertyId, PropertyDetailsUpdateReqDto updateRequest) {
		log.info("Updating property with id: {}", propertyId);

		// Find the existing property
		Property existingProperty = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceDoesNotExistsException(ExceptionCode.P102, ExceptionCode.P102.getDefaultMessage() + propertyId));

		// Update only provided fields
		boolean hasChanges = false;

		if (Objects.nonNull(updateRequest.getBuildingName()) &&
				!updateRequest.getBuildingName().equals(existingProperty.getBuildingName())) {
			existingProperty.setBuildingName(updateRequest.getBuildingName());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getLocation()) &&
				!updateRequest.getLocation().equals(existingProperty.getLocation())) {
			existingProperty.setLocation(updateRequest.getLocation());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getFloor()) &&
				!updateRequest.getFloor().equals(existingProperty.getFloor())) {
			existingProperty.setFloor(updateRequest.getFloor());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getNumberOfBhk()) &&
				!updateRequest.getNumberOfBhk().equals(existingProperty.getNumberOfBhk())) {
			existingProperty.setNumberOfBhk(updateRequest.getNumberOfBhk());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getNumberOfRk()) &&
				!updateRequest.getNumberOfRk().equals(existingProperty.getNumberOfRk())) {
			existingProperty.setNumberOfRk(updateRequest.getNumberOfRk());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getFurnishingStatus()) &&
				!updateRequest.getFurnishingStatus().equals(existingProperty.getFurnishingStatus())) {
			existingProperty.setFurnishingStatus(updateRequest.getFurnishingStatus());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getArea()) &&
				!updateRequest.getArea().equals(existingProperty.getArea())) {
			existingProperty.setArea(updateRequest.getArea());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getQuotedAmount()) &&
				!updateRequest.getQuotedAmount().equals(existingProperty.getQuotedAmount())) {
			existingProperty.setQuotedAmount(updateRequest.getQuotedAmount());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getCarParkingSlots()) &&
				!updateRequest.getCarParkingSlots().equals(existingProperty.getCarParkingSlots())) {
			existingProperty.setCarParkingSlots(updateRequest.getCarParkingSlots());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getComment()) &&
				!updateRequest.getComment().equals(existingProperty.getComment())) {
			existingProperty.setComment(updateRequest.getComment());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getBrokerName()) &&
				!updateRequest.getBrokerName().equals(existingProperty.getBrokerName())) {
			existingProperty.setBrokerName(updateRequest.getBrokerName());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getBrokerPhone()) &&
				!updateRequest.getBrokerPhone().equals(existingProperty.getBrokerPhone())) {
			existingProperty.setBrokerPhone(updateRequest.getBrokerPhone());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getListingType()) &&
				!updateRequest.getListingType().equals(existingProperty.getListingType())) {
			existingProperty.setListingType(updateRequest.getListingType());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getAvailabilityStatus()) &&
				!updateRequest.getAvailabilityStatus().equals(existingProperty.getAvailabilityStatus())) {
			existingProperty.setAvailabilityStatus(updateRequest.getAvailabilityStatus());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getLeaseEndDate()) &&
				!updateRequest.getLeaseEndDate().equals(existingProperty.getLeaseEndDate())) {
			existingProperty.setLeaseEndDate(updateRequest.getLeaseEndDate());
			hasChanges = true;
		}

		if (Objects.nonNull(updateRequest.getRentalEndDate()) &&
				!updateRequest.getRentalEndDate().equals(existingProperty.getRentalEndDate())) {
			existingProperty.setRentalEndDate(updateRequest.getRentalEndDate());
			hasChanges = true;
		}

		if (!hasChanges) {
			throw new ValidationException(ExceptionCode.N100, "No changes detected in the provided data");
		}

		// Save updated property
		Property updatedProperty = propertyRepository.save(existingProperty);
		log.info("Property updated successfully: {}", updatedProperty);

		// Return updated property details
		return modelMapper.map(updatedProperty, PropertyDetailRespDto.class);
	}

	@Transactional
	public PropertyDetailRespDto updatePropertyStatus(UUID propertyId, PropertyStatusUpdateReqDto statusUpdateRequest) {
		log.info("Updating property status with id: {}", propertyId);

		// Find the existing property
		Property existingProperty = propertyRepository.findById(propertyId)
				.orElseThrow(() -> new ResourceDoesNotExistsException(ExceptionCode.P102, ExceptionCode.P102.getDefaultMessage() + propertyId));

		AvailabilityStatus newStatus = statusUpdateRequest.getAvailabilityStatus();
		AvailabilityStatus currentStatus = existingProperty.getAvailabilityStatus();

		if (newStatus.equals(currentStatus)) {
			throw new ValidationException(ExceptionCode.N101, "Availability status same as previous status.");
		}

		// Clear all status-specific fields first
		clearStatusFields(existingProperty);

		// Update availability status
		existingProperty.setAvailabilityStatus(newStatus);

		// Set appropriate fields based on the new status
		switch (newStatus) {
			case LEASED:
				existingProperty.setLeasedOn(Objects.nonNull(statusUpdateRequest.getLeasedOn()) ? statusUpdateRequest.getLeasedOn() : LocalDateTime.now());
				existingProperty.setLeasedBy(statusUpdateRequest.getLeasedBy());
				existingProperty.setLeasedTo(statusUpdateRequest.getLeasedTo());
				existingProperty.setLeasedForAmount(statusUpdateRequest.getLeasedForAmount());
				break;

			case RENTED:
				existingProperty.setRentedOn(Objects.nonNull(statusUpdateRequest.getRentedOn()) ? statusUpdateRequest.getRentedOn() : LocalDateTime.now());
				existingProperty.setRentedBy(statusUpdateRequest.getRentedBy());
				existingProperty.setRentedTo(statusUpdateRequest.getRentedTo());
				existingProperty.setRentedForAmount(statusUpdateRequest.getRentedForAmount());
				break;

			case SOLD:
				existingProperty.setSoldOn(Objects.nonNull(statusUpdateRequest.getSoldOn()) ? statusUpdateRequest.getSoldOn() : LocalDateTime.now());
				existingProperty.setSoldBy(statusUpdateRequest.getSoldBy());
				existingProperty.setSoldTo(statusUpdateRequest.getSoldTo());
				existingProperty.setSoldForAmount(statusUpdateRequest.getSoldForAmount());
				break;

			case AVAILABLE:
				// For AVAILABLE status, all tracking fields remain null (already cleared above)
				break;

			default:
				throw new ValidationException(ExceptionCode.N101, "Invalid availability status: " + newStatus);
		}

		// Save updated property
		Property updatedProperty = propertyRepository.save(existingProperty);
		log.info("Property status updated successfully: {}", updatedProperty);

		// Return updated property details
		return modelMapper.map(updatedProperty, PropertyDetailRespDto.class);
	}

	private void clearStatusFields(Property property) {
		// Clear LEASED fields
		property.setLeasedOn(null);
		property.setLeasedBy(null);
		property.setLeasedTo(null);
		property.setLeasedForAmount(null);

		// Clear RENTED fields
		property.setRentedOn(null);
		property.setRentedBy(null);
		property.setRentedTo(null);
		property.setRentedForAmount(null);

		// Clear SOLD fields
		property.setSoldOn(null);
		property.setSoldBy(null);
		property.setSoldTo(null);
		property.setSoldForAmount(null);
	}

}
