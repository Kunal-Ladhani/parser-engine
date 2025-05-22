package com.parser.engine.helper;

import com.parser.engine.common.Constants;
import com.parser.engine.common.ExceptionCode;
import com.parser.engine.dto.PropertyExcelDto;
import com.parser.engine.exception.InvalidFileContentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */
@Slf4j
@Component
public class ExcelHelper {

	public Boolean hasExcelFormat(String contentType) {
		return Constants.EXCEL_SUPPORTED_TYPES.contains(contentType);
	}

	public List<PropertyExcelDto> extractDataFromExcel(InputStream inputStream) {
		List<PropertyExcelDto> rawData = new ArrayList<>();
		try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream)) {
			XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
			int rowIndex = 0;
			for (Row row : sheet) {
				if (rowIndex == 0) {
					rowIndex++;
					continue;
				}
				Iterator<Cell> cellIterator = row.iterator();
				int cellIndex = 0;
				PropertyExcelDto propertyExcelDto = new PropertyExcelDto();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					switch (cellIndex) {
//						case 0 -> propertyExcelDto.setCustomerId((int) cell.getNumericCellValue());
//						case 1 -> propertyExcelDto.setFirstName(cell.getStringCellValue());
//						case 2 -> propertyExcelDto.setLastName(cell.getStringCellValue());
//						case 3 -> propertyExcelDto.setCountry(cell.getStringCellValue());
//						case 4 -> propertyExcelDto.setTelephone((int) cell.getNumericCellValue());
						default -> {
						}
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


}
