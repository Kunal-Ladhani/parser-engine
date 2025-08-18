package com.parser.engine.service;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.request.PropertyCreateReqDto;
import com.parser.engine.dto.request.PropertyDetailsUpdateReqDto;
import com.parser.engine.dto.request.PropertyStatusUpdateReqDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PropertyService {

	Page<PropertySearchRespDto> fetchPropertyList(PropertySearchFilterDto propertySearchFilterDto, Pageable pageable);

	PropertyDetailRespDto fetchPropertyDetail(UUID propertyId);

	PropertyDetailRespDto updatePropertyDetail(UUID propertyId, PropertyDetailsUpdateReqDto updateRequest);

	PropertyDetailRespDto updatePropertyStatus(UUID propertyId, PropertyStatusUpdateReqDto statusUpdateRequest);

	PropertyDetailRespDto createProperty(PropertyCreateReqDto createRequest);

}
