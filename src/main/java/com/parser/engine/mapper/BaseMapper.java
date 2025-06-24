package com.parser.engine.mapper;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BaseMapper {

	@Named("getDateAdded")
	default String getDateAdded(Row row) {
		Cell cell = row.getCell(0); // Assuming date is in first column
		if (Objects.nonNull(cell) && CellType.NUMERIC.equals(cell.getCellType())) {
			return cell.getDateCellValue().toString();
		}
		return null;
	}

	@Named("getStringValue")
	default String getStringValue(Row row) {
		Cell cell = row.getCell(0); // This will be overridden by MapStruct
		if (Objects.nonNull(cell)) {
			switch (cell.getCellType()) {
				case STRING -> {
					return cell.getStringCellValue();
				}
				case NUMERIC -> {
					if (DateUtil.isCellDateFormatted(cell)) {
						return cell.getDateCellValue().toString();
					}
					return String.valueOf(cell.getNumericCellValue());
				}
				case BOOLEAN -> {
					return String.valueOf(cell.getBooleanCellValue());
				}
				case FORMULA -> {
					return cell.getCellFormula();
				}
				default -> {
					return null;
				}
			}
		}
		return null;
	}
}
