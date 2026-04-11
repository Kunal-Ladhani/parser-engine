package com.parser.engine.entity;

import com.parser.engine.common.Constants;
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
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "id")
@RequiredArgsConstructor
@Entity(name = "property")
public class Property extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = Constants.PropertyEntity.ID, updatable = false, nullable = false)
	private UUID id;

	// --------------------------  EXCEL FILE FIELDS --------------------------------

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

	@Column(name = PropertyEntity.DATE_ADDED)
	private LocalDateTime dateAdded;

	@Column(name = PropertyEntity.LEASE_END_DATE)
	private LocalDateTime leaseEndDate;

	@Column(name = PropertyEntity.RENTAL_END_DATE)
	private LocalDateTime rentalEndDate;

	// --------------------------  DASHBOARD BUTTON FIELDS --------------------------------

	@Column(name = PropertyEntity.LEASED_ON)
	private LocalDateTime leasedOn;

	@Column(name = PropertyEntity.LEASED_BY)
	private String leasedBy;

	@Column(name = PropertyEntity.LEASED_TO)
	private String leasedTo;

	@Column(name = PropertyEntity.LEASED_FOR_AMOUNT)
	private Double leasedForAmount;

	@Column(name = PropertyEntity.RENTED_ON)
	private LocalDateTime rentedOn;

	@Column(name = PropertyEntity.RENTED_BY)
	private String rentedBy;

	@Column(name = PropertyEntity.RENTED_TO)
	private String rentedTo;

	@Column(name = PropertyEntity.RENTED_FOR_AMOUNT)
	private Double rentedForAmount;

	@Column(name = PropertyEntity.SOLD_ON)
	private LocalDateTime soldOn;

	@Column(name = PropertyEntity.SOLD_BY)
	private String soldBy;

	@Column(name = PropertyEntity.SOLD_TO)
	private String soldTo;

	@Column(name = PropertyEntity.SOLD_FOR_AMOUNT)
	private Double soldForAmount;

}
