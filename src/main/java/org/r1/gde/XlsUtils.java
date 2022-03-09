package org.r1.gde;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.service.ComputeContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XlsUtils {

	private static final int TAILLE_LOT_BV = 15;

	static CellStyle getFieldNameStyle(Workbook wbk) {
		Font font = wbk.createFont();
		font.setFontHeightInPoints((short) 10);
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

	public static Row createBassinRow(Workbook workbook, Sheet sheet, int rowIndex, int indexColumn, String title1,
			String title2) {
		Row row = sheet.createRow(rowIndex);
		Cell cell = row.createCell(indexColumn);
		cell.setCellValue(title1);
		cell.setCellStyle(XlsUtils.getFieldNameStyle(workbook));
		cell = row.createCell(indexColumn + 1);
		cell.setCellValue(title2);
		cell.setCellStyle(XlsUtils.getFieldNameStyle(workbook));
		return row;
	}

	public static void mergeRow(ComputeContext computeContext, Sheet sheet, int rowIndex, int firstCol, int lastCol) {
		log.info("merge row=" + rowIndex + ", col1=" + firstCol + ", col2=" + lastCol);
		int index = sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol));
//		borderMergedCellRange(sheet.getMergedRegion(index), sheet);
	}

	public static void mergeCol(ComputeContext computeContext, Sheet sheet, int colIndex, int firstRow, int lastRow) {
		int index = sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, colIndex, colIndex));
//		borderMergedCellRange(sheet.getMergedRegion(index), sheet);
	}

	public static void mergeRowBottomBorder(ComputeContext computeContext, Sheet sheet, int rowIndex, int firstCol,
			int lastCol) {
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
		sheet.addMergedRegion(region);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void mergeRowLeftBorder(ComputeContext computeContext, Sheet sheet, int rowIndex, int firstCol,
			int lastCol) {
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
		sheet.addMergedRegion(region);
		RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
	}

	public static void makeAllBoldExceptBottomBorder(Sheet sheet, int firstRow, int lastRow, int firstCol,
			int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
	}

	public static void makeBottomBorder(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);

		RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void makeAllBoldExceptBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeLeftTopBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeLeftRightBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeTopRightBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeLeftRightBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeLeftBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeRightBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeRightTopBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeBoldBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeTopBottomBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeTopBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.MEDIUM);
	}

	public static void makeLeftBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.THIN);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeRightBorder(Cell c) {
		CellStyle cs = c.getCellStyle();
		cs.setBorderBottom(BorderStyle.THIN);
		cs.setBorderLeft(BorderStyle.THIN);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.THIN);
	}

	public static void makeAllBoldExceptTopBorder(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void makeBoldBorder(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
		CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
		RegionUtil.setBorderLeft(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static void mergeRowTopBorder(ComputeContext computeContext, Sheet sheet, int rowIndex, int firstCol,
			int lastCol) {
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
		sheet.addMergedRegion(region);
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
	}

	public static void mergeRowBothBorder(ComputeContext computeContext, Sheet sheet, int rowIndex, int firstCol,
			int lastCol) {
		CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol);
		sheet.addMergedRegion(region);
		RegionUtil.setBorderTop(BorderStyle.MEDIUM, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	public static Cell title1(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.NONE);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.NONE);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setWrapText(true);
		style.setFont(makeTitle1Font(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title2(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setWrapText(true);
		style.setFont(makeTitle2Font(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title2LeftBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setWrapText(true);
		style.setFont(makeTitle2Font(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell titleZone(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeZoneFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(StringUtils.upperCase(value));

		return cell;
	}

	public static Cell title3(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title3RightBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title3LeftBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title3LeftTopBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title3RightBottomBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell title3LeftBottomBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	private static CellStyle buildDefaultStyle(ComputeContext computeContext, Cell cell) {
//		if (cell.getCellStyle() == null) {
		return computeContext.workbook.createCellStyle();
//		} else {
//			return cell.getCellStyle();
//		}
	}

	private static CellStyle buildDefaultStyle(ComputeContext computeContext) {
		return computeContext.workbook.createCellStyle();
	}

	public static Cell title3BottomBorder(ComputeContext computeContext, Cell cell, String title) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeTitle3Font(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(title);

		return cell;
	}

	public static Cell standardCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}
	
	public static Cell standardCellVATop(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.TOP);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellNoBorder(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell fillCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardNoBorderCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

//		style.setBorderBottom(BorderStyle.THIN);
//		style.setBorderLeft(BorderStyle.THIN);
//		style.setBorderRight(BorderStyle.THIN);
//		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell decimalCellEmpty(ComputeContext computeContext, Cell cell) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		return cell;
	}

	public static Cell decimalCell(ComputeContext computeContext, Cell cell, double value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static void borderMergedSheet(Sheet sheet) {
		List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
		for (CellRangeAddress rangeAddress : mergedRegions) {
			RegionUtil.setBorderTop(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderLeft(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderRight(BorderStyle.THIN, rangeAddress, sheet);
			RegionUtil.setBorderBottom(BorderStyle.THIN, rangeAddress, sheet);
		}
	}

	public static void borderMergedCellRange(CellRangeAddress cra, Sheet sheet) {
		RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
		RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
	}

//	public static Cell standardCellDecimalNoComma(ComputeContext computeContext, Cell cell, String value) {
//		CellStyle style = buildDefaultStyle(computeContext, cell);
//
//		style.setBorderBottom(BorderStyle.THIN);
//		style.setBorderLeft(BorderStyle.THIN);
//		style.setBorderRight(BorderStyle.THIN);
//		style.setBorderTop(BorderStyle.THIN);
//
//		style.setAlignment(HorizontalAlignment.CENTER);
//		style.setVerticalAlignment(VerticalAlignment.CENTER);
//
//		style.setFont(makeStandardFont(computeContext));
//		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
//		style.setWrapText(true);
//
//		cell.setCellStyle(style);
//
//		cell.setCellValue(value);
//
//		return cell;
//	}

	public static Cell standardCellDecimal2Comma(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0.00"));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellDecimal1Comma(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));
		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0.0"));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell subTitleCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell objectifRetentionCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

//		style.setBorderBottom(BorderStyle.MEDIUM);
//		style.setBorderLeft(BorderStyle.MEDIUM);
//		style.setBorderRight(BorderStyle.MEDIUM);
//		style.setBorderTop(BorderStyle.THIN);
		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.TOP);

		style.setFont(makeBoldFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		SheetConditionalFormatting sheetCF = cell.getSheet().getSheetConditionalFormatting();

//		ConditionalFormattingRule ruleOrange = sheetCF.createConditionalFormattingRule("$C1<80");
		ConditionalFormattingRule ruleOrange = sheetCF.createConditionalFormattingRule(ComparisonOperator.LE, "80",
				null);
		PatternFormatting fillOrange = ruleOrange.createPatternFormatting();
		fillOrange.setFillBackgroundColor(IndexedColors.LIGHT_ORANGE.index);
		fillOrange.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

//		ConditionalFormattingRule ruleJaune= sheetCF.createConditionalFormattingRule("$C1>80 AND $C1<100");
		ConditionalFormattingRule ruleJaune = sheetCF.createConditionalFormattingRule(ComparisonOperator.BETWEEN,
				"80.001", "99.999");
		PatternFormatting fillJaune = ruleJaune.createPatternFormatting();
		fillJaune.setFillBackgroundColor(IndexedColors.YELLOW.index);
		fillJaune.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

//		ConditionalFormattingRule ruleVert = sheetCF.createConditionalFormattingRule("$C1>100");
		ConditionalFormattingRule ruleVert = sheetCF.createConditionalFormattingRule(ComparisonOperator.GE, "100",
				null);
		PatternFormatting fillVert = ruleVert.createPatternFormatting();
		fillVert.setFillBackgroundColor(IndexedColors.BRIGHT_GREEN.index);
		fillVert.setFillPattern(PatternFormatting.SOLID_FOREGROUND);

		ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[] { ruleOrange, ruleJaune, ruleVert };

		CellRangeAddress[] regions = new CellRangeAddress[] { CellRangeAddress.valueOf(getReference(cell)) };

		sheetCF.addConditionalFormatting(regions, cfRules);

		return cell;
	}

	public static Cell colorCell(ComputeContext computeContext, Cell cell, IndexedColors c) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setFillBackgroundColor(c.getIndex());

		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(c.getIndex());
		cell.setCellStyle(style);
		return cell;
	}

	public static Cell boldCell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeBoldFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellTopBorder(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellLeftRightBorder(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellLeftRightBorderDecimalNoComma(ComputeContext computeContext, Cell cell,
			String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.THIN);

		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell standardCellDecimalNoComma(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}
	
	public static Cell standardCellDecimalNoCommaVATop(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.TOP);

		style.setFont(makeStandardFont(computeContext));

		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell cell(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));
		style.setWrapText(true);

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell redBoldBorderTop(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}


	public static Cell redBoldBorderTopLeftRight(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell redBold(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));
		style.setWrapText(true);
		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}
	
	public static Cell redBoldVATop(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.TOP);

		style.setFont(makeRedBoldFont(computeContext));
		style.setWrapText(true);
		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell redBoldBorderLeftRight(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.THIN);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell backBlueBoldBorderTopLeftRight(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell redBoldBorderBottomDecimalNoComma(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static Cell redBoldDecimalNoComma(ComputeContext computeContext, Cell cell, String value) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);

		style.setDataFormat(computeContext.workbook.createDataFormat().getFormat("0"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		style.setFont(makeRedBoldFont(computeContext));

		cell.setCellStyle(style);

		cell.setCellValue(value);

		return cell;
	}

	public static CellStyle blankRow(ComputeContext computeContext) {
		CellStyle style = buildDefaultStyle(computeContext);

		style.setBorderBottom(BorderStyle.NONE);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.NONE);

		return style;
	}

	public static CellStyle blankRowBottomBorder(ComputeContext computeContext) {
		CellStyle style = buildDefaultStyle(computeContext);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.NONE);

		return style;
	}

	private static XSSFFont makeStandardFont(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setColor(IndexedColors.BLACK.getIndex());
		return font;
	}

	private static XSSFFont makeBoldFont(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		return font;
	}

	private static XSSFFont makeZoneFont(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontHeightInPoints((short) 14);
		font.setFontName("Arial");
		font.setBold(true);
		font.setColor(IndexedColors.RED.getIndex());
		return font;
	}

	private static XSSFFont makeRedBoldFont(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Arial");
		font.setColor(IndexedColors.RED.getIndex());
		font.setBold(true);
		return font;
	}

	private static XSSFFont makeTitle1Font(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		return font;
	}

	private static XSSFFont makeTitle2Font(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 14);
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		return font;
	}

	private static XSSFFont makeTitle3Font(ComputeContext computeContext) {
		XSSFFont font = computeContext.workbook.createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		return font;
	}

	public static Cell blankRowWithBorder(ComputeContext computeContext, Cell cell) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.MEDIUM);

		cell.setCellStyle(style);

		return cell;
	}

	public static Cell blankRowBottomBorder(ComputeContext computeContext, Cell cell) {
		CellStyle style = buildDefaultStyle(computeContext, cell);

		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.NONE);
		style.setBorderRight(BorderStyle.NONE);
		style.setBorderTop(BorderStyle.NONE);

		cell.setCellStyle(style);

		return cell;
	}

	public static String getReference(Cell cell) {
		return "'" + cell.getSheet().getSheetName() + "'!" + CellReference.convertNumToColString(cell.getColumnIndex())
				+ (cell.getRowIndex() + 1);
	}

}
