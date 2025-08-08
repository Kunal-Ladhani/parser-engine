package com.parser.engine.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.parser.engine.common.Constants;
import com.parser.engine.common.Constants.PropertyEntity;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "id")
@RequiredArgsConstructor
@Entity(name = "property")
public class Property extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = Constants.FileEntity.ID, updatable = false, nullable = false)
	private UUID id;

	@Column(name = PropertyEntity.BUILDING_NAME)
	private String buildingName;

	@Column(name = PropertyEntity.LOCATION)
	private String location;

	@Column(name = PropertyEntity.FLOOR)
	private String floor;

	@Column(name = PropertyEntity.NUMBER_OF_BHK)
	private Double numberOfBhk;

	@Column(name = PropertyEntity.NUMBER_OF_RK)
	private Double numberOfRk;

	@Enumerated(EnumType.STRING)
	@Column(name = PropertyEntity.FURNISHING_STATUS)
	private FurnishingStatus furnishingStatus;

	@Column(name = PropertyEntity.AREA)
	private Double area;

	@Column(name = PropertyEntity.QUOTED_AMOUNT)
	private Double quotedAmount;

	@Column(name = PropertyEntity.CAR_PARKING_SLOTS)
	private Integer carParkingSlots;

	@Column(name = PropertyEntity.COMMENT)
	private String comment;

	@Column(name = PropertyEntity.BROKER_NAME)
	private String brokerName;

	@Column(name = PropertyEntity.BROKER_PHONE)
	private String brokerPhone;

	@Enumerated(EnumType.STRING)
	@Column(name = PropertyEntity.LISTING_TYPE)
	private ListingType listingType;

	@Enumerated(EnumType.STRING)
	@Column(name = PropertyEntity.AVAILABILITY_STATUS)
	private AvailabilityStatus availabilityStatus;

	@Column(name = PropertyEntity.TENURE_END_DATE)
	private ZonedDateTime leaseOrRentExpiryDate;

}
