package com.parser.engine.dao;

import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.dto.PropertySearchFilter;
import com.parser.engine.dto.response.PropertyDetailsRespDto;
import com.parser.engine.entity.Property;
import com.parser.engine.mapper.PropertyMapper;
import com.parser.engine.repository.PropertyRepository;
import com.parser.engine.spec.PropertySpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PropertyDao {

	private final PropertyMapper propertyMapper;
	private final PropertyRepository propertyRepository;

	@Autowired
	public PropertyDao(PropertyMapper propertyMapper, PropertyRepository propertyRepository) {
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

	public Page<PropertyDetailsRespDto> getPropertyDetailsPageByFilter(PropertySearchFilter propertySearchFilter, Pageable pageable) {
		log.info("Searching properties with filter: {}", propertySearchFilter);

		// Create specification using criteria API
		Specification<Property> spec = PropertySpecification.withFilters(propertySearchFilter);

		// Execute query with specification
		Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);

		// Convert to DTOs
		Page<PropertyDetailsRespDto> result = propertyPage.hasContent() ? propertyPage.map(propertyMapper::toResponseDto) : Page.empty(pageable);

		log.info("Number of properties matching the filter: {}", result.getTotalElements());
		return result;
	}

}
