package com.parser.engine.service.impl;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dao.PropertyDao;
import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.exception.ServiceException;
import com.parser.engine.service.PropertyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
//            if (!propertySearchFilterDto.isAtleastOneFilterPresent()) {
//                throw new ValidationException(ExceptionCode.V101, "No filter parameter present.");
//            }

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
}
