package com.parser.engine.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.parser.engine.enums.AvailabilityStatus;

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
public class PropertyStatusUpdateReqDto {

    private AvailabilityStatus availabilityStatus;

    // Fields for LEASED status
    private LocalDateTime leasedOn;
    private String leasedBy;
    private String leasedTo;
    private Double leasedForAmount;

    // Fields for RENTED status
    private LocalDateTime rentedOn;
    private String rentedBy;
    private String rentedTo;
    private Double rentedForAmount;

    // Fields for SOLD status
    private LocalDateTime soldOn;
    private String soldBy;
    private String soldTo;
    private Double soldForAmount;
}
