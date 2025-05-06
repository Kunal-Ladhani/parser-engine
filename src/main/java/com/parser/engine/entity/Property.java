package com.parser.engine.entity;

import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@Entity(name = "property")
public class Property extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "building_name")
	private String BuildingName;

	@Column(name = "location")
	private String location;

	@Column(name = "floor")
	private String floor;

	@Enumerated(EnumType.STRING)
	@Column(name = "furnishing_status")
	private FurnishingStatus furnishingStatus;

	@Column(name = "area")
	private Double area;

	@Column(name = "quoted_amount")
	private Double quotedAmount;

	@Column(name = "car_parking_slots")
	private Integer carParkingSlots;

	@Column(name = "comment")
	private String comment;

	@Column(name = "broker_name")
	private String brokerName;

	@Column(name = "broker_phone")
	private String brokerPhone;

	@Enumerated(EnumType.STRING)
	@Column(name = "listing_type")
	private ListingType listingType;

	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status")
	private AvailabilityStatus availabilityStatus;

	@Column(name = "tenure_end_date")
	private ZonedDateTime leaseOrRentExpiryDate;

}
