package com.parser.engine.spec;

import com.parser.engine.dto.filter.PropertySearchFilterDto;
import com.parser.engine.entity.Property;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
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

	public static Specification<Property> withFilters(PropertySearchFilterDto propertySearchFilterDto) {

		// dashboard row 1
		String buildingName = propertySearchFilterDto.getBuildingName();
		String location = propertySearchFilterDto.getLocation();
		Integer numberOfBhk = CommonHelper.parseInt(propertySearchFilterDto.getNumberOfBhk());
		Integer numberOfRk = CommonHelper.parseInt(propertySearchFilterDto.getNumberOfRk());

		// dashboard row 2
		Double area = CommonHelper.parseDouble(propertySearchFilterDto.getArea());
		String furnishingStatusString = propertySearchFilterDto.getFurnishingStatus();
		String floor = propertySearchFilterDto.getFloor();
		Integer carParkingSlots = CommonHelper.parseInt(propertySearchFilterDto.getCarParkingSlots());

		// dashboard row 3
		Double quotedAmount = CommonHelper.parseDouble(propertySearchFilterDto.getQuotedAmount());
		String listingTypeString = propertySearchFilterDto.getListingType();
		String availabilityString = propertySearchFilterDto.getAvailabilityStatus();

		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			// -------------- ROW 1 filters -----------------

			if (StringUtils.hasText(buildingName)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("buildingName")), "%" + buildingName.toLowerCase() + "%"));
			}

			// Location filter (case-insensitive partial match)
			if (StringUtils.hasText(location)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
			}

			// BHK filter
			if (Objects.nonNull(numberOfBhk)) {
				predicates.add(criteriaBuilder.equal(root.get("numberOfBhk"), numberOfBhk));
			}

			// RK filter
			if (Objects.nonNull(numberOfRk)) {
				predicates.add(criteriaBuilder.equal(root.get("numberOfRk"), numberOfRk));
			}

			// -------------- ROW 2 filters -----------------

			// Area filter (greater than or equal to match)
			if (Objects.nonNull(area)) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("area"), area));
			}

			// Furnishing status filter
			if (StringUtils.hasText(furnishingStatusString)) {
				try {
					FurnishingStatus furnishingStatus = FurnishingStatus.valueOf(furnishingStatusString.toUpperCase());
					predicates.add(criteriaBuilder.equal(root.get("furnishingStatus"), furnishingStatus));
				} catch (IllegalArgumentException e) {
					log.error("FurnishingStatus not found: {}", furnishingStatusString);
				}
			}

			// Floor filter (case-insensitive partial match)
			if (StringUtils.hasText(floor)) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("floor")), "%" + floor.toLowerCase() + "%"));
			}

			// Car parking slots filter (greater than or equal to)
			if (Objects.nonNull(carParkingSlots)) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("carParkingSlots"), carParkingSlots));
			}

			// -------------- ROW 3 filters -----------------

			// Quoted amount filter (exact match)
			if (Objects.nonNull(quotedAmount)) {
				predicates.add(criteriaBuilder.equal(root.get("quotedAmount"), quotedAmount));
			}

			// ListingStatus status filter
			if (StringUtils.hasText(listingTypeString)) {
				try {
					ListingType listingType = ListingType.valueOf(listingTypeString.toUpperCase());
					predicates.add(criteriaBuilder.equal(root.get("listingType"), listingType));
				} catch (IllegalArgumentException e) {
					log.error("ListingType not found: {}", listingTypeString);
				}
			}

			// Availability status filter
			if (StringUtils.hasText(availabilityString)) {
				try {
					AvailabilityStatus availabilityStatus = AvailabilityStatus.valueOf(availabilityString.toUpperCase());
					predicates.add(criteriaBuilder.equal(root.get("availabilityStatus"), availabilityStatus));
				} catch (IllegalArgumentException e) {
					log.error("AvailabilityStatus not found: {}", availabilityString);
				}
			}

			// Combine all predicates with AND
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}