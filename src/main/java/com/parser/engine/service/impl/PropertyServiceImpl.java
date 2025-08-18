package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dao.PropertyDao;
import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.request.PropertyCreateReqDto;
import com.parser.engine.dto.request.PropertyDetailsUpdateReqDto;
import com.parser.engine.dto.request.PropertyStatusUpdateReqDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.exception.ServiceException;
import com.parser.engine.exception.ValidationException;
import com.parser.engine.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class PropertyServiceImpl implements PropertyService {

	private final PropertyDao propertyDao;

	@Autowired
	public PropertyServiceImpl(PropertyDao propertyDao) {
		this.propertyDao = propertyDao;
	}

	@Override
	public Page<PropertySearchRespDto> fetchPropertyList(PropertySearchFilterDto propertySearchFilterDto, Pageable pageable) {
		try {
			log.info("Fetching property details with the filter: {}", propertySearchFilterDto.toString());

			Page<PropertySearchRespDto> propertyDetailsRespDtoPage = propertyDao.getPropertyDetailsPageByFilter(propertySearchFilterDto, pageable);
			log.info("Property details page by filter: {}", propertyDetailsRespDtoPage);

			return propertyDetailsRespDtoPage;
		} catch (Exception e) {
			log.error("Error occurred while searching for property: {}", e.getMessage());
			throw new ServiceException(ExceptionCode.P101, ExceptionCode.P101.getDefaultMessage());
		}
	}

	@Override
	public PropertyDetailRespDto fetchPropertyDetail(UUID propertyId) {
		return propertyDao.getPropertyDetailById(propertyId);
	}

	@Override
	public PropertyDetailRespDto updatePropertyDetail(UUID propertyId, PropertyDetailsUpdateReqDto updateRequest) {
		try {
			log.info("Property update request received for propertyId: {} with data: {}", propertyId, updateRequest);
			// Validate that at least one field is provided
			if (!updateRequest.hasAnyField()) {
				throw new ValidationException(ExceptionCode.N101, "At least one field must be provided for update");
			}
			return propertyDao.updateProperty(propertyId, updateRequest);
		} catch (Exception e) {
			log.error("Error occurred while updating property with id {}: {}", propertyId, e.getMessage());
			throw new ServiceException(ExceptionCode.P103, ExceptionCode.P103.getDefaultMessage() + e.getMessage());
		}
	}

	@Override
	public PropertyDetailRespDto updatePropertyStatus(UUID propertyId, PropertyStatusUpdateReqDto statusUpdateRequest) {
		try {
			log.info("Property status update request received for propertyId: {} with data: {}", propertyId, statusUpdateRequest);
			// Validate that availability status is provided
			if (Objects.isNull(statusUpdateRequest.getAvailabilityStatus())) {
				throw new ValidationException(ExceptionCode.N101, "Availability status must be provided for status update");
			}
			return propertyDao.updatePropertyStatus(propertyId, statusUpdateRequest);
		} catch (Exception e) {
			log.error("Error occurred while updating property status with id {}: {}", propertyId, e.getMessage());
			throw new ServiceException(ExceptionCode.P103, ExceptionCode.P103.getDefaultMessage() + e.getMessage());
		}
	}

	@Override
	public PropertyDetailRespDto createProperty(PropertyCreateReqDto createRequest) {
		try {
			log.info("Property creation request received with data: {}", createRequest);
			// Validate that required fields are provided
			if (!createRequest.hasRequiredFields()) {
				String errorMessage = createRequest.getValidationErrorMessage();
				throw new ValidationException(ExceptionCode.N101, errorMessage != null ? errorMessage : "Required fields are missing");
			}
			return propertyDao.createProperty(createRequest);
		} catch (ValidationException e) {
			log.error("Validation error during property creation: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Error occurred while creating property: {}", e.getMessage());
			throw new ServiceException(ExceptionCode.P104, ExceptionCode.P104.getDefaultMessage() + e.getMessage());
		}
	}
}
