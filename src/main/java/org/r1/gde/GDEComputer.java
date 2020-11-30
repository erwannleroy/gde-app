package org.r1.gde;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.validation.constraints.Size;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.buf.ByteChunk.ByteOutputChannel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.json.ByteSourceJsonBootstrapper;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GDEComputer {

	private Workbook workbook = new XSSFWorkbook();
	private int rowIndexDimensionnement = 0;
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
		log.info("Recalcul du résultat");
		computingResult = new ComputingResult();
		workbook = new XSSFWorkbook();

		generateDimensionnementSheet();
		try {
			ByteArrayOutputStream bytes = writeToFile();
			computingResult.setInProgress(false);
			computingResult.setComputationOk(true);
			// String bytesString = Base64.getEncoder().encodeToString(bytes.toByteArray());
			computingResult.setXls(bytes.toByteArray());
			bytes.flush();
			bytes.close();
			log.info("Génération du résultat terminé");
		} catch (IOException e) {
			computingResult.setInProgress(false);
			computingResult.setComputationOk(false);
			log.error("Impossible de générer le fichier Excel", e);
		}
	}

	private ByteArrayOutputStream writeToFile() throws IOException {
		System.out.println("Ecriture du fichier Excel");
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		if (true) {
			log.info("Ecriture du fichier : " + fileLocation);
			FileOutputStream file = new FileOutputStream(fileLocation);
			workbook.write(file);
			file.close();
		}
		workbook.write(bytes);
		bytes.close();
		return bytes;
	}

	private void generateDimensionnementSheet() {
		log.info("Génération de l'onglet Dimensionnement");
		rowIndexDimensionnement = 0;
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
				rowIndexDimensionnement++;
			}

			int column = 0;
			while (column < tailleLotBV + 2) {
				sheet.autoSizeColumn(column);
				column++;
			}
		}
	}

	private void generateLotBassins(Sheet sheet, List<BassinVersant> bassinsLot) {

		XlsUtils.createTitleRow2(workbook, sheet, "Paramètres hydrauliques des bassins versants associés aux ouvrages",
				rowIndexDimensionnement);

		rowIndexDimensionnement++;
		int indexColumn = 0;

		Row ouvrageRow = sheet.createRow(rowIndexDimensionnement);
		rowIndexDimensionnement++;
		Row superficieRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Superficie du BV alimentant le bassin (ha) :");
		rowIndexDimensionnement++;
		Row longueurRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Longueur hydraulique du bassin versant (m)");
		rowIndexDimensionnement++;
		Row deniveleRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Dénivelé du bassin versant (m)");
		rowIndexDimensionnement++;
		Row penteRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn, "Pente (%)");
		rowIndexDimensionnement++;
		Row ruissellementRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Coefficient de ruissellement du BV :");
		rowIndexDimensionnement++;
		rowIndexDimensionnement++;
		Row pluieRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Temps de retour et durée de la pluie de référence choisis :  ");
		rowIndexDimensionnement++;

		XlsUtils.createTitleRow2(workbook, sheet, "Dimensionnement des bassins", rowIndexDimensionnement);
		rowIndexDimensionnement++;

		Row precipitationsRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Quantité max de précipitations i(t;T) \npour une durée de pluie t (min) et pour \nune période de retour T (années)",
				"i(t;T) en mm =");
		rowIndexDimensionnement++;
		Row volumeEauRow = XlsUtils.createBassinRow(workbook, sheet, rowIndexDimensionnement, indexColumn,
				"Volume d'eau V à retenir dans le décanteur (m3)", "V (m3) =");

		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		for (BassinVersant bv : bassinsLot) {
			Cell nomCell = ouvrageRow.createCell(indexColumn);
			nomCell.setCellValue(bv.nomOuvrage);
			nomCell.setCellStyle(XlsUtils.getRedBoldStyle(workbook));

			Cell superficieCell = superficieRow.createCell(indexColumn);
			superficieCell.setCellValue(bv.surface / 10000);
			Cell longueurCell = longueurRow.createCell(indexColumn);
			longueurCell.setCellValue(bv.longueur);
			Cell deniveleCell = deniveleRow.createCell(indexColumn);
			deniveleRow.createCell(indexColumn).setCellValue(bv.denivele);
			Cell penteCell = penteRow.createCell(indexColumn);
			penteCell.setCellFormula(String.format("(%s%s/%s%s)*100",
					CellReference.convertNumToColString(deniveleCell.getColumnIndex()), deniveleCell.getRowIndex()+1,
					CellReference.convertNumToColString(longueurCell.getColumnIndex()), longueurCell.getRowIndex()+1));
			Cell ruissellementCell = ruissellementRow.createCell(indexColumn);
			ruissellementCell.setCellValue(0.9);

			pluieRow.createCell(indexColumn).setCellValue("2H/2ANS");

			Cell precipitationsCell = precipitationsRow.createCell(indexColumn);
			precipitationsCell.setCellValue(59.8);

			Cell volEauCell = volumeEauRow.createCell(indexColumn);
			String volEauFormula = String.format("(%s%s/1000)*(%s%s*10000)*%s%s",
					CellReference.convertNumToColString(precipitationsCell.getColumnIndex()),
					precipitationsCell.getRowIndex()+1,
					CellReference.convertNumToColString(superficieCell.getColumnIndex()), superficieCell.getRowIndex()+1,
					CellReference.convertNumToColString(ruissellementCell.getColumnIndex()+1),
					ruissellementCell.getRowIndex());
			log.info("Formule du volume d'eau : " + volEauFormula);
			volEauCell.setCellFormula(volEauFormula);
			volEauCell.setCellStyle(XlsUtils.getRedBoldStyle(workbook));
			indexColumn++;
		}

	}

	private void buildDimensionnementHeader(Sheet sheet) {
		Row header = sheet.createRow(rowIndexDimensionnement);

		int firstRow = rowIndexDimensionnement;
		int lastRow = rowIndexDimensionnement;
		int firstCol = 0;
		int lastCol = 2 + tailleLotBV;
		sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));

		String title = "Objectif de rétention pour chaque sous-bassin versant de la mine";

		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Arial");
		font.setFontHeightInPoints((short) 16);
		font.setBold(true);
		headerStyle.setFont(font);

		Cell headerCell = header.createCell(0);
		headerCell.setCellValue(title);
		headerCell.setCellStyle(headerStyle);

		// une ligne vide
		rowIndexDimensionnement++;
		sheet.createRow(rowIndexDimensionnement);

		rowIndexDimensionnement++;
	}

	public void updateDecanteurs(List<Decanteur> decanteurs) {
		this.decanteurs = decanteurs;
		recompute();
	}

}
