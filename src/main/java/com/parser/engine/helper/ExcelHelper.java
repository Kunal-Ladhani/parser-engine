package com.parser.engine.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.parser.engine.common.Constants;
import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.exception.InvalidFileContentException;
import com.parser.engine.mapper.ExcelRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */
@Slf4j
@Component
public class ExcelHelper {

	private final ExcelRowMapper excelRowMapper;

	@Autowired
	public ExcelHelper(ExcelRowMapper excelRowMapper) {
		this.excelRowMapper = excelRowMapper;
	}

	public Boolean hasExcelFormat(String contentType) {
		return Constants.EXCEL_SUPPORTED_TYPES.contains(contentType);
	}

	// ----------------------------------- POSITION BASED IMPL ---------------------------------------

	/**
	 * Extracts data from Excel file using row position for mapping
	 *
	 * @param inputStream Excel file input stream
	 * @return List of PropertyExcelDto objects mapped from Excel data
	 */
	public List<PropertyExcelDto> extractDataFromExcel(InputStream inputStream) {
		List<PropertyExcelDto> rawData = new ArrayList<>();
		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream)) {
			// int numberOfSheets = xssfWorkbook.getNumberOfSheets();

			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
			int rowIndex = 0;
			for (Row row : sheet) {
				// Skip header row
				if (rowIndex == 0) {
					rowIndex++;
					continue;
				}

				// Iterate over cells in other row
				Iterator<Cell> cellIterator = row.iterator();
				int cellIndex = 0;
				PropertyExcelDto propertyExcelDto = new PropertyExcelDto();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					switch (cellIndex) {
//                        case 0 ->
//                            propertyExcelDto.setCustomerId((int) cell.getNumericCellValue());
//                        case 1 ->
//                            propertyExcelDto.setFirstName(cell.getStringCellValue());
//                        case 2 ->
//                            propertyExcelDto.setLastName(cell.getStringCellValue());
//                        case 3 ->
//                            propertyExcelDto.setCountry(cell.getStringCellValue());
//                        case 4 ->
//                            propertyExcelDto.setTelephone((int) cell.getNumericCellValue());
//                        default -> {
//                        }
					}
					cellIndex++;
				}
				rawData.add(propertyExcelDto);
			}
		} catch (IOException e) {
			throw new InvalidFileContentException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage() + e.getMessage());
		}
		return rawData;
	}

	// ----------------------------------- HEADER BASED IMPL ---------------------------------------

	/**
	 * Extracts data from Excel file using header names for mapping
	 *
	 * @param inputStream Excel file input stream
	 * @return List of PropertyExcelDto objects mapped from Excel data
	 */
	public List<PropertyExcelDto> extractDataFromExcelByHeaders(InputStream inputStream) {
		List<PropertyExcelDto> rawData = new ArrayList<>();
		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream)) {
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

			// Get header row
			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				throw new InvalidFileContentException(ExceptionCode.F103, "Excel file is empty or has no headers");
			}

			// Create header to column index mapping
			Map<String, Integer> headerToIndexMap = new HashMap<>();
			for (int i = 0; i < headerRow.getLastCellNum(); i++) {
				Cell cell = headerRow.getCell(i);
				if (cell != null) {
					headerToIndexMap.put(cell.getStringCellValue().trim(), i);
				}
			}
			log.info("Header to Index Map: {}", headerToIndexMap);


			// Get DTO field to header mapping
			Map<String, String> fieldToHeaderMap = getFieldToHeaderMapping(PropertyExcelDto.class);
			log.info("Field to Header Map: {}", fieldToHeaderMap);

			// Process data rows
//			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
//				Row row = sheet.getRow(rowIndex);
//				if (row == null) {
//					break;
//				}
//
//				PropertyExcelDto dto = new PropertyExcelDto();
//				for (Map.Entry<String, String> entry : fieldToHeaderMap.entrySet()) {
//					String fieldName = entry.getKey();
//					String headerName = entry.getValue();
//					Integer columnIndex = headerToIndexMap.get(headerName);
//
//					if (columnIndex != null) {
//						Cell cell = row.getCell(columnIndex);
//						if (cell != null) {
//							setFieldValue(dto, fieldName, cell);
//						}
//					}
//				}
//				rawData.add(dto);
//			}
//

			Iterator<Row> rowIterator = sheet.iterator();

			// Skip header
			if (rowIterator.hasNext()) {
				rowIterator.next();
			}

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (isRowEmpty(row)) continue;

				PropertyExcelDto dto = new PropertyExcelDto();

				for (Map.Entry<String, String> entry : fieldToHeaderMap.entrySet()) {
					String fieldName = entry.getKey();
					String headerName = entry.getValue();
					Integer columnIndex = headerToIndexMap.get(headerName);

					if (columnIndex != null) {
						Cell cell = row.getCell(columnIndex);
						if (cell != null) {
							setFieldValue(dto, fieldName, cell);
						}
					}
				}
				rawData.add(dto);
			}

		} catch (IOException e) {
			throw new InvalidFileContentException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage() + e.getMessage());
		}
		return rawData;
	}

	private Map<String, String> getFieldToHeaderMapping(Class<?> dtoClass) {
		Map<String, String> mapping = new HashMap<>();
		for (Field field : dtoClass.getDeclaredFields()) {
			JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
			if (Objects.nonNull(jsonProperty)) {
				mapping.put(field.getName(), jsonProperty.value());
			}
		}
		return mapping;
	}

	private void setFieldValue(PropertyExcelDto dto, String fieldName, Cell cell) {
		try {
			Field field = PropertyExcelDto.class.getDeclaredField(fieldName);
			field.setAccessible(true);

			switch (cell.getCellType()) {
				case STRING -> field.set(dto, cell.getStringCellValue());
				case NUMERIC -> {
					if (DateUtil.isCellDateFormatted(cell)) {
						field.set(dto, cell.getDateCellValue().toString());
					} else {
						field.set(dto, String.valueOf(cell.getNumericCellValue()));
					}
				}
				case BOOLEAN -> field.set(dto, String.valueOf(cell.getBooleanCellValue()));
				case FORMULA -> field.set(dto, cell.getCellFormula());
				default -> field.set(dto, null);
			}
		} catch (Exception e) {
			log.warn("Failed to set value for field {}: {}", fieldName, e.getMessage());
		}
	}


	private boolean isRowEmpty(Row row) {
		for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
			Cell cell = row.getCell(cellIndex);
			if (cell != null && cell.getCellType() != CellType.BLANK) {
				if (cell.getCellType() == CellType.STRING && !cell.getStringCellValue().trim().isEmpty()) {
					return false;
				}
				if (cell.getCellType() != CellType.STRING) {
					return false;
				}
			}
		}
		return true;
	}

	// ---------------------------------- MAPSTRUCT IMPL ---------------------------------------

	/**
	 * Extracts data from Excel file using MapStruct for mapping
	 *
	 * @param inputStream Excel file input stream
	 * @return List of PropertyExcelDto objects mapped from Excel data
	 */
	public List<PropertyExcelDto> extractDataFromExcelWithMapStruct(InputStream inputStream) {
		List<PropertyExcelDto> rawData = new ArrayList<>();
		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream)) {
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);

			// Skip header row and process data rows
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (Objects.isNull(row)) {
					continue;
				}

				PropertyExcelDto dto = excelRowMapper.rowToDto(row);
				rawData.add(dto);
			}
		} catch (IOException e) {
			throw new InvalidFileContentException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage() + e.getMessage());
		}
		return rawData;
	}
}
