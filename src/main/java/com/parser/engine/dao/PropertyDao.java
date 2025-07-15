package com.parser.engine.dao;

import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.dto.response.PropertyDetailRespDto;
import com.parser.engine.dto.response.PropertySearchRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.exception.ResourceDoesNotExistsException;
import com.parser.engine.mapper.PropertyMapper;
import com.parser.engine.repository.PropertyRepository;
import com.parser.engine.spec.PropertySpecification;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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
		log.info("saving properties data: {}", propertyExcelDtoList);
		List<Property> entities = propertyExcelDtoList.stream()
				.map(propertyMapper::toEntity)
				.collect(Collectors.toList());
		log.info("List of property entities: {}", entities);
		propertyRepository.saveAll(entities);
	}

	public Page<PropertySearchRespDto> getPropertyDetailsPageByFilter(PropertySearchFilterDto propertySearchFilterDto, Pageable pageable) {
		log.info("Searching properties with filter: {}", propertySearchFilterDto);

		// Create specification using criteria API
		Specification<Property> spec = PropertySpecification.withFilters(propertySearchFilterDto);

		// Execute query with specification
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

}
