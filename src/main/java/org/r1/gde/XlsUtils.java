package org.r1.gde;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsUtils {

	private static final int TAILLE_LOT_BV = 15;


	static CellStyle getRedBoldStyle(Workbook wbk) {
		Font font = wbk.createFont();
		 font.setFontHeightInPoints((short)10);
		 font.setFontName("Arial");
		 font.setColor(IndexedColors.RED.getIndex());
		 font.setBold(true);
		 font.setItalic(false);
		 
		CellStyle style = wbk.createCellStyle();
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}
	
	static CellStyle getFieldNameStyle(Workbook wbk) {
		Font font = wbk.createFont();
		 font.setFontHeightInPoints((short)10);
		 font.setFontName("Arial");
		 font.setColor(IndexedColors.BLACK.getIndex());
		 font.setBold(true);
		 font.setItalic(false);
		 
		CellStyle style = wbk.createCellStyle();
		style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style.setFont(font);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.LEFT);
		return style;
	}

	public static Row createBassinRow(Workbook workbook, Sheet sheet, int rowIndex, int indexColumn, String value) {
		Row row = sheet.createRow(rowIndex);
		Cell cell = row.createCell(indexColumn);
		cell.setCellValue(value);
		cell.setCellStyle(XlsUtils.getFieldNameStyle(workbook));
		return row;
	}

	
	public static void createTitleRow2(Workbook workbook, Sheet sheet, String title, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		int firstRow = rowIndex;
		int lastRow = rowIndex;
		int firstCol = 0;
		int lastCol = 2 + XlsUtils.TAILLE_LOT_BV;
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = row.createCell(0);
		headerCell.setCellValue(title);
		headerCell.setCellStyle(headerStyle);
	}

	public static void createWhiteRow(Workbook workbook, Sheet sheet, int rowIndex) {
		sheet.createRow(rowIndex);
	}

	public static Row createBassinRow(Workbook workbook, Sheet sheet, int rowIndex, int indexColumn,
			String title1, String title2) {
		Row row = sheet.createRow(rowIndex);
		Cell cell = row.createCell(indexColumn);
		cell.setCellValue(title1);
		cell.setCellStyle(XlsUtils.getFieldNameStyle(workbook));
		cell = row.createCell(indexColumn+1);
		cell.setCellValue(title2);
		cell.setCellStyle(XlsUtils.getFieldNameStyle(workbook));
		return row;
	}

}
