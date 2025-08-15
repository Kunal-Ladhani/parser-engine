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

		public static final String ID = "id";
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
		public static final String NUMBER_OF_BHK = "number_of_bhk";
		public static final String NUMBER_OF_RK = "number_of_rk";
		public static final String DATE_ADDED = "date_added";
		public static final String LEASE_END_DATE = "lease_end_date";
		public static final String RENTAL_END_DATE = "rental_end_date";

		private PropertyEntity() {
		}

	}

	public static class FileEntity {

		public static final String ID = "id";
		public static final String FILE_NAME = "file_name";
		public static final String FILE_TYPE = "file_type";
		public static final String FILE_PROCESSING_STATUS = "file_processing_status";
		public static final String S3_KEY = "s3_key";
		public static final String AWS_KEY = "aws_key";
		public static final String SIZE_IN_BYTES = "size_in_bytes";
		public static final String ETAG = "etag";
		public static final String BUCKET_NAME = "bucket_name";
		public static final String CONTENT_TYPE = "content_type";
		public static final String UPLOADED_AT = "uploaded_at";
		public static final String UPLOADED_BY = "uploaded_by";
		public static final String DELETED_AT = "deleted_at";
		public static final String DELETED_BY = "deleted_by";
		public static final String IS_DELETED = "is_deleted";
		public static final String IS_PROCESSED = "is_processed";
		public static final String PROCESSED_AT = "processed_at";
		public static final String PROCESSED_BY = "processed_by";

		private FileEntity() {
		}

	}
}
