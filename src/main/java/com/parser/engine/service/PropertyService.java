package com.parser.engine.service;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.response.PropertyDetailsRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PropertyService {

	Page<PropertyDetailsRespDto> getPropertyDetails(PropertySearchFilterDto propertySearchFilterDto, Pageable pageable);

}
