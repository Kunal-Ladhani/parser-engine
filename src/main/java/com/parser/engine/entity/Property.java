package com.parser.engine.entity;

import com.parser.engine.common.Constants.PropertyEntity;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "id")
@RequiredArgsConstructor
@Entity(name = "property")
public class Property extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

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
	private LocalDateTime leaseOrRentExpiryDate;

}
