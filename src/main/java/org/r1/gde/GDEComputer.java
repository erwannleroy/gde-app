package org.r1.gde;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.validation.constraints.Size;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GDEComputer {

	private Workbook workbook = new XSSFWorkbook();
	private int rowIndex = 0;
	private int tailleLotBV = 15;
	private int tailleLotDEC = 15;

	private List<BassinVersant> bassins;
	private List<Decanteur> decanteurs;

	private ComputingResult computingResult;

	public ComputingResult getComputingResult() {
		return computingResult;
	}

	public void updateBassins(List<BassinVersant> bassins) {
		this.bassins = bassins;
		recompute();
	}

	private void recompute() {
		computingResult = new ComputingResult();
		workbook = new XSSFWorkbook();
		generateDimensionnementSheet();
		try {
			writeToFile();
			computingResult.setInProgress(false);
			computingResult.setComputationOk(true);
		} catch (IOException e) {
			computingResult.setInProgress(false);
			computingResult.setComputationOk(false);
			log.error("Impossible de générer le fichier Excel", e);
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

		if (bassins != null && !bassins.isEmpty()) {
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
		int indexColumn = 0;

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
			superficieRow.createCell(indexColumn).setCellValue(bv.surface / 10000);
			Cell longueurCell = longueurRow.createCell(indexColumn);
			longueurCell.setCellValue(bv.longueur);
			Cell deniveleCell = deniveleRow.createCell(indexColumn);
			deniveleRow.createCell(indexColumn).setCellValue(bv.denivele);
			Cell penteCell = penteRow.createCell(indexColumn);
			penteCell.setCellType(CellType.FORMULA);
			penteCell.setCellFormula(String.format("(%s%s/%s%s)/100",
					CellReference.convertNumToColString(deniveleCell.getColumnIndex()), deniveleCell.getRowIndex(),
					CellReference.convertNumToColString(longueurCell.getColumnIndex()), longueurCell.getRowIndex()));
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

	public void updateDecanteurs(List<Decanteur> decanteurs) {
		this.decanteurs = decanteurs;
		recompute();
	}

}
