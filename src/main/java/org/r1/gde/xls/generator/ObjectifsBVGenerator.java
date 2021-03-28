package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BassinVersant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ObjectifsBVGenerator extends SheetGenerator {

	private int rowIndexDimensionnement = 0;
	private int TAILLE_LOT_BV = 15;
	private int TAILLE_COLUMN_1 = 8;
	private Map<String, String> volumesBV;
	private static final String TITLE_SHEET = "Objectifs BV";

	@Autowired
	private ParametresGenerator parametresGenerator;

	@Override
	public void startGeneration() {

		log.info("Génération de l'onglet Dimensionnement");

		sheet = workbook().createSheet(TITLE_SHEET);

		sheet.setColumnWidth(0, TAILLE_COLUMN_1);

		
		rowIndexDimensionnement = 0;
		volumesBV = new HashMap<>();

		generateTitleBlock();

		generateLotsBassins();

	}

	private void generateLotsBassins() {
		if (bassins() != null && !bassins().isEmpty()) {
			int nbBV = bassins().size();
			int currentLot = 0;
			int nbLotTotal = (int) Math.ceil((double) nbBV / TAILLE_LOT_BV);
			while (currentLot <= nbLotTotal - 1) {
				int startLot = currentLot * TAILLE_LOT_BV;
				int endLot = Math.min(nbBV, startLot + TAILLE_LOT_BV);
				generateLotBassins(bassins().subList(startLot, endLot));
				currentLot++;
				rowIndexDimensionnement++;
			}

			int column = 0;
			while (column < TAILLE_LOT_BV + 2) {
				sheet.autoSizeColumn(column);
				column++;
			}
		}

	}

	private void generateLotBassins(List<BassinVersant> bassinsLot) {

		int indexColumn = 1;
		
		int columnSize  = bassinsLot.size() + 1;
		// une ligne vide
		XlsUtils.mergeRowBottomBorder(computeContext, sheet, rowIndexDimensionnement, indexColumn, columnSize);

		rowIndexDimensionnement++;
		
		int firstRow = rowIndexDimensionnement;

		// titre
		Row title2ParamRow = sheet.createRow(rowIndexDimensionnement);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexDimensionnement, indexColumn, columnSize);
		Cell title2ParamCell = title2ParamRow.createCell(indexColumn);
		title2(computeContext, title2ParamCell, "Paramètres hydrauliques des bassins versants associés aux ouvrages");

		rowIndexDimensionnement++;

		// entete tableau
		
		Row ouvrageRow = sheet.createRow(rowIndexDimensionnement);

		rowIndexDimensionnement++;

		Row superficieRow = sheet.createRow(rowIndexDimensionnement);
		Cell titleSuperficie = superficieRow.createCell(indexColumn);
		title3(computeContext, titleSuperficie, "Superficie du BV alimentant le bassin (ha) :");
		rowIndexDimensionnement++;

		Row longueurRow = sheet.createRow(rowIndexDimensionnement);
		Cell titleLongueur = longueurRow.createCell(indexColumn);
		title3(computeContext, titleLongueur, "Longueur hydraulique du bassin versant (m)");
		rowIndexDimensionnement++;

		Row deniveleRow = sheet.createRow(rowIndexDimensionnement);
		Cell titleDenivele = deniveleRow.createCell(indexColumn);
		title3(computeContext, titleDenivele, "Dénivelé du bassin versant (m)");
		rowIndexDimensionnement++;

		Row penteRow = sheet.createRow(rowIndexDimensionnement);
		Cell titlePente = penteRow.createCell(indexColumn);
		title3(computeContext, titlePente, "Pente (%)");
		rowIndexDimensionnement++;

		Row ruissellementRow = sheet.createRow(rowIndexDimensionnement);
		Cell titleRuissellement = ruissellementRow.createCell(indexColumn);
		title3(computeContext, titleRuissellement, "Coefficient de ruissellement du BV :");

		rowIndexDimensionnement++;

		// une ligne vide
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexDimensionnement, indexColumn, columnSize+1);

		rowIndexDimensionnement++;

		Row pluieRow = sheet.createRow(rowIndexDimensionnement);
		Cell titlePluie = pluieRow.createCell(indexColumn);
		title3(computeContext, titlePluie, "Temps de retour et durée de la pluie de référence choisis :  ");

		rowIndexDimensionnement++;

		// une ligne vide
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexDimensionnement, indexColumn, columnSize+1);

		rowIndexDimensionnement++;

		Row title2DimensionnementRow = sheet.createRow(rowIndexDimensionnement);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexDimensionnement, indexColumn, columnSize+1);
		Cell title2DimCell = title2DimensionnementRow.createCell(indexColumn);
		title2(computeContext, title2DimCell, "Dimensionnement des bassins");

		rowIndexDimensionnement++;

		Row precipitationsRow = sheet.createRow(rowIndexDimensionnement);
		Cell titlePrecipitations = precipitationsRow.createCell(indexColumn);
		title3(computeContext, titlePrecipitations,
				"Quantité max de précipitations i(t;T) \npour une durée de pluie t (min) et pour \nune période de retour T (années)");
		Cell titlePrecipitationsCol2 = precipitationsRow.createCell(indexColumn + 1);
		title3(computeContext, titlePrecipitationsCol2, "i(t;T) en mm =");

		rowIndexDimensionnement++;

		Row volumeEauRow = sheet.createRow(rowIndexDimensionnement);
		Cell titleVolume = volumeEauRow.createCell(indexColumn);
		title3BottomBorder(computeContext, titleVolume, "Volume d'eau V à retenir dans le décanteur (m3)");
		Cell titleVolumeCol2 = volumeEauRow.createCell(indexColumn + 1);
		title3BottomBorder(computeContext, titleVolumeCol2, "V (m3) =");

		rowIndexDimensionnement++;

		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		for (BassinVersant bv : bassinsLot) {
			Cell nomCell = ouvrageRow.createCell(indexColumn);
			nomCell.setCellValue(bv.nomOuvrage);
			redBoldBorderTop(computeContext, nomCell, bv.nomOuvrage);

			Cell superficieCell = superficieRow.createCell(indexColumn);
			if (bv.surface != null) {
				standardCellDecimal2Comma(computeContext, superficieCell, "").setCellValue(bv.surface / 10000);
			} 
//			else {
//				standardCell(computeContext, superficieCell, "")
//						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.DIM_SURFACE_PARAM));
//			}

			Cell longueurCell = longueurRow.createCell(indexColumn);
			if (bv.longueur != null) {
				standardCellDecimalNoComma(computeContext, longueurCell, "").setCellValue(bv.longueur);
			} 
//			else {
//				standardCell(computeContext, longueurCell, "")
//						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.DIM_LONGUEUR_PARAM));
//			}

			Cell deniveleCell = deniveleRow.createCell(indexColumn);
			if (bv.denivele != null) {
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(bv.denivele);
			} 
//			else {
//				standardCell(computeContext, deniveleCell, "")
//						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.DIM_DENIVELE_PARAM));
//			}

			Cell penteCell = penteRow.createCell(indexColumn);
			standardCellDecimalNoComma(computeContext, penteCell, "");
			penteCell.setCellFormula(String.format("(%s%s/%s%s)*100",
					CellReference.convertNumToColString(deniveleCell.getColumnIndex()), deniveleCell.getRowIndex() + 1,
					CellReference.convertNumToColString(longueurCell.getColumnIndex()),
					longueurCell.getRowIndex() + 1));

			Cell ruissellementCell = ruissellementRow.createCell(indexColumn);
			standardCell(computeContext, ruissellementCell, "")
					.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));

			Cell pluieCell = pluieRow.createCell(indexColumn);
			boldCell(computeContext, pluieCell, ParametresGenerator.DIM_TPS_RETOUR_DUREE_PLUIE_DEFAULT);

			Cell precipitationsCell = precipitationsRow.createCell(indexColumn);
			standardCellTopBorder(computeContext, precipitationsCell, "").setCellValue(59.8);

			Cell volEauCell = volumeEauRow.createCell(indexColumn);
			String volEauFormula = String.format("INT((%s%s/1000)*(%s%s*10000)*%s%s)",
					CellReference.convertNumToColString(precipitationsCell.getColumnIndex()),
					precipitationsCell.getRowIndex() + 1,
					CellReference.convertNumToColString(superficieCell.getColumnIndex()),
					superficieCell.getRowIndex() + 1,
					CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
					ruissellementCell.getRowIndex() + 1);

			volumesBV.put(bv.nomOuvrage, XlsUtils.getReference(volEauCell));

			log.info("Formule du volume d'eau : " + volEauFormula);
			redBoldBorderBottomDecimalNoComma(computeContext, volEauCell, "").setCellFormula(volEauFormula);
			indexColumn++;
		}

		XlsUtils.makeBoldBorder(sheet, firstRow, rowIndexDimensionnement-1, 1, indexColumn-1);
		
	}

	public String getReferenceVolumeEauBV(String nomBV) {
		return volumesBV.get(nomBV);
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexDimensionnement);

		// une colonne vide
		int indexColumn = 1;
		
		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT_BV + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Objectif de rétention pour chaque sous-bassin versant de la mine ";
		Cell headerCell = titleRow.createCell(1);
		title1(computeContext, headerCell, title).setCellFormula("CONCATENATE(\"" + title + "\","
				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexDimensionnement++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
