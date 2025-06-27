package com.parser.engine.mapper;

import com.parser.engine.dto.PropertyExcelDto;
import org.apache.poi.ss.usermodel.Row;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ExcelRowMapper extends BaseMapper {

	@Mapping(source = "row", target = "dateAdded", qualifiedByName = "getDateAdded")
	@Mapping(source = "row", target = "buildingName", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "location", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "floor", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "furniture", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "area", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "carPark", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "quoted", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "comment", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "agent", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "amenities", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "quotedInCr", qualifiedByName = "getStringValue")
	@Mapping(source = "row", target = "areaInCarpet", qualifiedByName = "getStringValue")
	PropertyExcelDto rowToDto(Row row);

	default List<PropertyExcelDto> rowsToDtos(List<Row> rows) {
		return rows.stream()
				.map(this::rowToDto)
				.collect(Collectors.toList());
	}
}
