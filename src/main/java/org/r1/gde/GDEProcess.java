package org.r1.gde;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

//@Slf4j
public class GDEProcess {

	private File bvFile;
	private File decanteursFile;
	private List<BassinVersant> bassins = new ArrayList<>();
	private Map<String, Object> map;
	private Workbook workbook = new XSSFWorkbook();
	private int rowIndex = 0;
	private int tailleLotBV = 15;

	public GDEProcess(File bvFile, File decanteursFile) {
		this.bvFile = bvFile;
		this.decanteursFile = decanteursFile;
	}

	public void start() {
		try {
			parseBV();

			generateDimensionnementSheet();

			writeToFile();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeToFile() throws IOException {
		System.out.println("Ecriture du fichier Excel");
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();

	}

	private void generateDimensionnementSheet() {
		System.out.println("Génération de l'onglet Dimensionnement");
		
		Sheet sheet = workbook.createSheet("Dimensionnement BV");

		buildDimensionnementHeader(sheet);

		int nbBV = bassins.size();
		int nbLot = 0;
		while (nbLot <= nbBV / tailleLotBV) {
			int startLot = nbLot * nbBV / tailleLotBV;
			int endLot = Math.min(nbBV, startLot + tailleLotBV);
			generateLotBassins(sheet, bassins.subList(startLot, endLot));
			nbLot++;
			rowIndex++;
		}

	}

	private void generateLotBassins(Sheet sheet, List<BassinVersant> bassinsLot) {

		Row headerLot = sheet.createRow(rowIndex);
		rowIndex++;

		String titleLot = "Paramètres hydrauliques des bassins versants associés aux ouvrages";

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);


		Cell headerCell = headerLot.createCell(0);
		headerCell.setCellValue(titleLot);
		headerCell.setCellStyle(headerStyle);
		int indexColumn= 0;
		
		rowIndex++;
		Row ouvrageRow = sheet.createRow(rowIndex);
		rowIndex++;
		Row superficieRow = sheet.createRow(rowIndex);
		
		superficieRow.createCell(indexColumn).setCellValue("Superficie du BV alimentant le bassin (ha) :");
		rowIndex++;
		Row longueurRow = sheet.createRow(rowIndex);
		
		longueurRow.createCell(indexColumn).setCellValue("Longueur hydraulique du bassin versant (m)");
		rowIndex++;
		Row deniveleRow = sheet.createRow(rowIndex);
		deniveleRow.createCell(indexColumn).setCellValue("Dénivelé du bassin versant (m)");
		rowIndex++;
		Row penteRow = sheet.createRow(rowIndex);
		penteRow.createCell(indexColumn).setCellValue("Pente (%)");
		rowIndex++;
		Row ruissellementRow = sheet.createRow(rowIndex);
		ruissellementRow.createCell(indexColumn).setCellValue("Coefficient de ruissellement du BV :");
		rowIndex++;
		rowIndex++;
		Row pluieRow = sheet.createRow(rowIndex);
		pluieRow.createCell(indexColumn).setCellValue("Temps de retour et durée de la pluie de référence choisis :  ");
		rowIndex++;
		
		indexColumn++;
		indexColumn++;
		
		for (BassinVersant bv : bassinsLot) {
			ouvrageRow.createCell(indexColumn).setCellValue(bv.nomOuvrage);
			superficieRow.createCell(indexColumn).setCellValue(bv.surface/10000);
			Cell longueurCell = longueurRow.createCell(indexColumn);
			longueurCell.setCellValue(bv.longueur);
			Cell deniveleCell = deniveleRow.createCell(indexColumn);
			deniveleRow.createCell(indexColumn).setCellValue(bv.denivele);
			Cell penteCell = penteRow.createCell(indexColumn);
			penteCell.setCellType(CellType.FORMULA);
			penteCell.setCellFormula(String.format("(%s%s/%s%s)/100", 
					CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
					deniveleCell.getRowIndex(),
					CellReference.convertNumToColString(longueurCell.getColumnIndex()),
					longueurCell.getRowIndex()
					));
			ruissellementRow.createCell(indexColumn).setCellValue(0.9);

			indexColumn++;
		}

	}

	private void buildDimensionnementHeader(Sheet sheet) {
		Row header = sheet.createRow(rowIndex);
		rowIndex++;

		String title = "Objectif de rétention pour chaque sous-bassin versant de la mine";

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue(title);
		headerCell.setCellStyle(headerStyle);

		rowIndex++;
	}

	private void parseBV() throws IOException, ParseException {
		System.out.println("Parsing du bv " + bvFile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		InputStream dbf = new FileInputStream(bvFile);

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			System.out.println("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				System.out.println("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				bassins.add(recordBVToBassinVersant(rec));
			}
		}
		System.out.println("Parcours de " + bassins.size() + " bassins versants");
		System.out.println("Bassins:" + bassins);
	}

	private BassinVersant recordBVToBassinVersant(DbfRecord rec) throws ParseException {
		BassinVersant bv = new BassinVersant();
		Map<String, Object> map = rec.toMap();
		bv.setNomOuvrage(map.get(BassinVersant.NOM_OUVRAGE_FIELD).toString());
		bv.setDenivele(Integer.parseInt(map.get(BassinVersant.DENIVELE_FIELD).toString()));
		bv.setLongueur(Integer.parseInt(map.get(BassinVersant.LONGUEUR_FIELD).toString()));
		bv.setSurface(Double.parseDouble(map.get(BassinVersant.SURFACE_FIELD).toString()));
		bv.setPerformance(Performance.toPerformance(map.get(BassinVersant.PERFORMANCE_FIELD).toString()));
		return bv;
	}

}
