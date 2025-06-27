package com.parser.engine.common;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class Constants {

	public static final Set<String> EXCEL_SUPPORTED_TYPES = Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel");

	public static class SpringProfile {

		public static final String DEV = "dev";
		public static final String PROD = "prod";

		private SpringProfile() {
		}
	}

	public static class HttpHeaders {
		private HttpHeaders() {
		}
	}

	public static class PropertyEntity {

		public static final String BUILDING_NAME = "building_name";
		public static final String LOCATION = "location";
		public static final String FLOOR = "floor";
		public static final String FURNISHING_STATUS = "furnishing_status";
		public static final String AREA = "area";
		public static final String QUOTED_AMOUNT = "quoted_amount";
		public static final String CAR_PARKING_SLOTS = "car_parking_slots";
		public static final String COMMENT = "comment";
		public static final String BROKER_NAME = "broker_name";
		public static final String BROKER_PHONE = "broker_phone";
		public static final String LISTING_TYPE = "listing_type";
		public static final String AVAILABILITY_STATUS = "availability_status";
		public static final String TENURE_END_DATE = "tenure_end_date";
		public static final String NUMBER_OF_BHK = "number_of_bhk";
		public static final String NUMBER_OF_RK = "number_of_rk";

		private PropertyEntity() {
		}

	}
}
