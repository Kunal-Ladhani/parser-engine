package com.parser.engine.dto.request;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.parser.engine.enums.AvailabilityStatus;
import com.parser.engine.enums.FurnishingStatus;
import com.parser.engine.enums.ListingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDetailsUpdateReqDto {

    private String buildingName;

    private String location;

    private String floor;

    private Double numberOfBhk;

    private Double numberOfRk;

    private FurnishingStatus furnishingStatus;

    private Double area;

    private Double quotedAmount;

    private Integer carParkingSlots;

    private String comment;

    private String brokerName;

    private String brokerPhone;

    private ListingType listingType;

    private AvailabilityStatus availabilityStatus;

    private LocalDateTime leaseEndDate;

    private LocalDateTime rentalEndDate;

    /**
     * Check if at least one field is provided for update
     *
     * @return true if at least one field is non-null
     */
    public boolean hasAnyField() {
        return Objects.nonNull(buildingName)
                || Objects.nonNull(location)
                || Objects.nonNull(floor)
                || Objects.nonNull(numberOfBhk)
                || Objects.nonNull(numberOfRk)
                || Objects.nonNull(furnishingStatus)
                || Objects.nonNull(area)
                || Objects.nonNull(quotedAmount)
                || Objects.nonNull(carParkingSlots)
                || Objects.nonNull(comment)
                || Objects.nonNull(brokerName)
                || Objects.nonNull(brokerPhone)
                || Objects.nonNull(listingType)
                || Objects.nonNull(availabilityStatus)
                || Objects.nonNull(leaseEndDate)
                || Objects.nonNull(rentalEndDate);
    }
}
