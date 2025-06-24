package com.parser.engine.spec;

import com.parser.engine.common.Constants.PropertyEntity;
import com.parser.engine.dto.PropertySearchFilter;
import com.parser.engine.entity.Property;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.helper.CommonHelper;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PropertySpecification {

	public static Specification<Property> withFilters(PropertySearchFilter propertySearchFilter) {

		String floor = propertySearchFilter.getFloor();
		String location = propertySearchFilter.getLocation();
		String furnishingStatusString = propertySearchFilter.getFurnishingStatus();
		// parse numeric values
		Double numberOfBhk = CommonHelper.parseDouble(propertySearchFilter.getNumberOfBhk());
		Double numberOfRk = CommonHelper.parseDouble(propertySearchFilter.getNumberOfRk());
		Double area = CommonHelper.parseDouble(propertySearchFilter.getArea());
		Double quotedAmount = CommonHelper.parseDouble(propertySearchFilter.getQuotedAmount());
		Integer carParkingSlots = CommonHelper.parseInt(propertySearchFilter.getCarParkingSlots());

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Location filter (case-insensitive partial match)
			if (StringUtils.hasText(location)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(PropertyEntity.LOCATION)), "%" + location.toLowerCase() + "%"));
			}

			// Floor filter (case-insensitive partial match)
			if (StringUtils.hasText(floor)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(PropertyEntity.FLOOR)), "%" + floor.toLowerCase() + "%"));
			}

			// Furnishing status filter
			if (StringUtils.hasText(furnishingStatusString)) {
				try {
					FurnishingStatus furnishingStatus = FurnishingStatus.valueOf(furnishingStatusString.toUpperCase());
					predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.FURNISHING_STATUS), furnishingStatus));
				} catch (IllegalArgumentException e) {
					log.error("FurnishingStatus not found");
				}
			}

			// Area filter (exact match)
			if (Objects.nonNull(area)) {
				predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.AREA), area));
			}

			// Quoted amount filter (exact match)
			if (Objects.nonNull(quotedAmount)) {
				predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.QUOTED_AMOUNT), quotedAmount));
			}

			// Car parking slots filter (exact match)
			if (Objects.nonNull(carParkingSlots)) {
				predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.CAR_PARKING_SLOTS), carParkingSlots));
			}

			// BHK filter
			if (Objects.nonNull(numberOfBhk)) {
				predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.NUMBER_OF_BHK), numberOfBhk));
			}

			// RK filter
			if (Objects.nonNull(numberOfRk)) {
				predicates.add(criteriaBuilder.equal(root.get(PropertyEntity.NUMBER_OF_RK), numberOfRk));
			}

			// Combine all predicates with AND
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}