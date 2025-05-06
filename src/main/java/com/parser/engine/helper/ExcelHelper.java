package com.parser.engine.helper;

import com.parser.engine.common.Constants;
import com.parser.engine.common.ExceptionCode;
import com.parser.engine.exception.InvalidFileContentException;
import com.parser.engine.exception.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.STRING;

/**
 * @author Kunal Ladhani
 * @since v1.0
 */

@Slf4j
@Component
public class ExcelHelper {

	public XSSFWorkbook getWorkbookInstance() {
		return new XSSFWorkbook();
	}

	public static Boolean hasExcelFormat(@NotNull MultipartFile file) {
		if (Constants.EXCEL_SUPPORTED_TYPES.contains(file.getContentType())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@SneakyThrows
	@NotNull
	public List<String> extractDataFromExcel(InputStream inputStream) {
		try {
			var xssfWorkbook = new XSSFWorkbook(inputStream);
			var sheet = xssfWorkbook.getSheetAt(0);
			var rows = sheet.iterator();
			var values = new ArrayList<String>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();
				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}
				var cellsInRow = currentRow.iterator();
				int cellIdx = 0;
				while (cellsInRow.hasNext()) {
					var currentCell = cellsInRow.next();
					if (cellIdx == 0 && ObjectUtils.isNotEmpty(currentCell.getStringCellValue())) {
						values.add(currentCell.getStringCellValue());
					}
					cellIdx++;
				}
			}
			xssfWorkbook.close();
			if (values.isEmpty()) {
				throw new ValidationException(ExceptionCode.V102, String.format(ExceptionCode.V102.getDefaultMessage(), Constants.UPLOAD_CONTENT));
			}
			return values;
		} catch (IOException e) {
			throw new InvalidFileContentException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage() + e.getMessage());
		}
	}

	public void writeFile(Map<String, Object[]> data, OutputStream outputStream) throws IOException {
		XSSFWorkbook wbTemplate = new XSSFWorkbook();

		try (SXSSFWorkbook workbook = new SXSSFWorkbook(wbTemplate)) {
			workbook.setCompressTempFiles(true);

			SXSSFSheet sheet = workbook.createSheet("Sheet1");
			sheet.setRandomAccessWindowSize(100);// keep 100 rows in memory, exceeding rows will be flushed to disk
			int rownum = 0;

			Set<String> keyset = data.keySet();
			TreeSet<String> keys = new TreeSet<>(keyset);
			for (String key : keys) {
				Row row = sheet.createRow(rownum++);
				Object[] objArr = data.get(key);
				int cellnum = 0;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (obj instanceof Date date)
						cell.setCellValue(date);
					else if (obj instanceof Boolean val)
						cell.setCellValue(val);
					else if (obj instanceof String val)
						cell.setCellValue(val);
					else if (obj instanceof Double val)
						cell.setCellValue((val));
				}
			}
			try {
				workbook.write(outputStream);
				outputStream.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static void writeHeadersToExcel(@NotNull XSSFSheet sheet, @NotNull String[] headers) {
		Row row = sheet.createRow(0);
		for (int colCount = 0; colCount < headers.length; ++colCount) {
			Cell cell = row.createCell(colCount);
			cell.setCellValue(headers[colCount]);
		}
	}

	public List<HashMap<String, Object>> convertExcelData(InputStream inputStream, boolean storeColumnIndex)
			throws IOException, ValidationException {
		ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		HashMap<String, Integer> columnHeadersMap = new HashMap<>();
		Iterator<Sheet> sheetIterator;
		ZipSecureFile.setMinInflateRatio(0);
		try (Workbook workbook = WorkbookFactory.create(inputStream)) {
			sheetIterator = workbook.iterator();
		}

		if (sheetIterator.hasNext()) {
			Sheet sheet = sheetIterator.next();
			Iterator<Row> rowIterator = sheet.iterator();
			if (rowIterator.hasNext()) { //First row in sheet
				Row firstRow = rowIterator.next();
				Iterator<Cell> cellIterator = firstRow.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell != null && cell.getCellType() == STRING) {
						String headerKey = storeColumnIndex ? String.valueOf(cell.getColumnIndex()) : cell.getStringCellValue().trim();
						if (!headerKey.isEmpty()) {
							if (columnHeadersMap.containsKey(headerKey)) {
								throw new ValidationException(ExceptionCode.V101, "Duplicate column headers found.");
							}
							columnHeadersMap.put(headerKey, cell.getColumnIndex());
						}
					}
				}
			}
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				HashMap<String, Object> rowDataMap = new HashMap<>();
				for (Map.Entry<String, Integer> columnHeader : columnHeadersMap.entrySet()) {
					Cell valueCell = row.getCell(columnHeader.getValue(), Row.MissingCellPolicy.RETURN_NULL_AND_BLANK);
					Object cellValue = mapCellValue(valueCell);
					if (ObjectUtils.isNotEmpty(cellValue)) {
						rowDataMap.put(columnHeader.getKey(), cellValue);
					}
				}
				if (ObjectUtils.isNotEmpty(row.getCell(0))) {
					result.add(rowDataMap);
				}
			}
		}
		return result;
	}

	private static Object mapCellValue(Cell valueCell) throws ValidationException {
		if (valueCell != null) {
			return switch (valueCell.getCellType()) {
				case NUMERIC -> valueCell.getNumericCellValue();
				case STRING -> valueCell.getStringCellValue();
				case BOOLEAN -> valueCell.getBooleanCellValue();
				case FORMULA -> throw new ValidationException(null, "Excel formula not supported.");
				default -> null;
			};
		}
		return null;
	}

	public List<String> extractNumericValue(InputStream inputStream) throws ValidationException, InvalidFileContentException {
		try {
			var workbook = new XSSFWorkbook(inputStream);
			var worksheet = workbook.getSheetAt(0);
			var negativePinCodeList = new ArrayList<String>();
			for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); ++i) {
				var row = worksheet.getRow(i);
				var cellValue = getCellValue(row);
				if (ObjectUtils.isNotEmpty(cellValue)) {
					negativePinCodeList.add(cellValue);
				}
			}
			if (negativePinCodeList.isEmpty()) {
				throw new ValidationException(ExceptionCode.V102, String.format(ExceptionCode.V102.getDefaultMessage(), Constants.UPLOAD_CONTENT));
			}
			return negativePinCodeList;
		} catch (IOException e) {
			throw new InvalidFileContentException(ExceptionCode.F103, ExceptionCode.F103.getDefaultMessage() + e.getMessage());
		}
	}

	private String getCellValue(XSSFRow row) {
		try {
			var cellValue = (long) row.getCell(0).getNumericCellValue();
			if (cellValue != 0.0) {
				return String.valueOf(cellValue).replace(".0", "");
			} else {
				return null;
			}
		} catch (Exception ex) {
			log.info("Exception occurred: {}", ex.getMessage(), ex);
			return null;
		}
	}
}
