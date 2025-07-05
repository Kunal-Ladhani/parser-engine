package com.parser.engine.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@Builder
public class PropertySearchFilter {

    private String numberOfRk;
    private String numberOfBhk;
    private String location;
    private String floor;
    private String furnishingStatus;
    private String area;
    private String quotedAmount;
    private String carParkingSlots;

    public boolean isAtleastOneFilterPresent() {
        String builder = numberOfRk
                + numberOfBhk
                + location
                + floor
                + furnishingStatus
                + area
                + quotedAmount
                + carParkingSlots;
        return StringUtils.hasText(builder
                .replace("null", "")
                .replace("\\[\\]", ""));
    }
}
