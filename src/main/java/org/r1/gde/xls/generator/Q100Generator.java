package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.decanteur.BassinVersant;
import org.r1.gde.model.decanteur.Ouvrage;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.model.exutoire.Creek;
import org.r1.gde.model.exutoire.Exutoire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Q100Generator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Q100 EI";
	private static final int TAILLE_LOT = 15;

	@Autowired
	ParametresGenerator parametresGenerator;

	@Override
	protected void startGeneration() {
		log.info("Génération de l'onglet Exutoire");

		sheet = workbook().createSheet(TITLE_SHEET);

		sheet.setColumnWidth(0, 2);

		rowIndexExutoire = 0;

		generateTitleBlock();

		generateLegend();

		if (creeks() != null) {
			generateCreeks();
		}

		int column = 0;
		while (column < TAILLE_LOT + 2) {
			sheet.autoSizeColumn(column);
			column++;
		}
	}

	private void generateCreeks() {

		List<Creek> remainList = creeks();
		List<Creek> creeksSubList = new ArrayList<Creek>();

		int nbExutoires = 0;

		for (Creek creek : remainList) {
			creeksSubList.add(creek);
			nbExutoires += creek.getExutoires().size();

			if (nbExutoires >= TAILLE_LOT || creeks().indexOf(creek) == (remainList.size() - 1)) {
				generateLotCreek(creeksSubList);
				creeksSubList.clear();
				nbExutoires = 0;
			}
			rowIndexExutoire++;
		}

	}

	private void generateLotCreek(List<Creek> creeks) {

		// une colonne vide
		int indexColumn = 1;

//		// une ligne vide
//		XlsUtils.mergeRowBottomBorder(computeContext, sheet, rowIndexExutoire, indexColumn, TAILLE_LOT + 2);

		rowIndexExutoire++;

		int firstRow = rowIndexExutoire;

//		// titre
//		Row title2ParamRow = sheet.createRow(rowIndexExutoire);
//		XlsUtils.mergeRowLeftBorder(computeContext, sheet, rowIndexExutoire, indexColumn, TAILLE_LOT_DEC + 2);
//		Cell title2ParamCell = title2ParamRow.createCell(indexColumn);
//		titleZone(computeContext, title2ParamCell, zone.nom);

		rowIndexExutoire++;

		// entete tableau
		Row creekRow = sheet.createRow(rowIndexExutoire);
//		Cell creekTitleCell = creekRow.createCell(indexColumn);
//		title3LeftTopBorder(computeContext, creekTitleCell, "");
//		Cell bvTitleCellCol2 = creekRow.createCell(indexColumn);
//		title3LeftTopBorder(computeContext, bvTitleCellCol2, "");

		rowIndexExutoire++;

		Row exutoireRow = sheet.createRow(rowIndexExutoire);
		Cell exutoireCell = exutoireRow.createCell(indexColumn);

		rowIndexExutoire++;

		Row superficieRow = sheet.createRow(rowIndexExutoire);
		Cell titleSuperficie = superficieRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleSuperficie, "Superficie BV (ha) :");
		rowIndexExutoire++;

		Row longueurRow = sheet.createRow(rowIndexExutoire);
		Cell titleLongueur = longueurRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleLongueur, "Longueur hydraulique BV (m)");
		rowIndexExutoire++;

		Row deniveleRow = sheet.createRow(rowIndexExutoire);
		Cell titleDenivele = deniveleRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleDenivele, "Dénivelé BV (m)");
		rowIndexExutoire++;

		Row penteRow = sheet.createRow(rowIndexExutoire);
		Cell titlePente = penteRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titlePente, "Pente BV (%)");
		rowIndexExutoire++;

		Row ruissellementRow = sheet.createRow(rowIndexExutoire);
		Cell titleRuissellement = ruissellementRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleRuissellement, "Coefficient de ruissellement");

		rowIndexExutoire++;

		Row ecoulementRow = sheet.createRow(rowIndexExutoire);
		Cell titleEcoulement = ecoulementRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleEcoulement, "Vitesse d'écoulement (m/s)");

		rowIndexExutoire++;

		Row tempsRetourRow = sheet.createRow(rowIndexExutoire);
		Cell titleTempsRetour = tempsRetourRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleTempsRetour, "Temps de retour choisi :");
		Cell titleTempsRetourCol2 = tempsRetourRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleTempsRetourCol2, "100 ans");

		rowIndexExutoire++;

		Row calculTpsConcentrationRow = sheet.createRow(rowIndexExutoire);
		Cell titleCalculTpsConcentration = calculTpsConcentrationRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleCalculTpsConcentration, "Calcul du temps de concentration (en min)");
		Cell titleCalculTpsConcentrationCol2 = calculTpsConcentrationRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculTpsConcentrationCol2, "Tc=");

		rowIndexExutoire++;

		Row tpsConcentrationRetenuRow = sheet.createRow(rowIndexExutoire);
		Cell titleTpsConcentrationRetenu = tpsConcentrationRetenuRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleTpsConcentrationRetenu, "Temps de concentration retenu (en min)");
		Cell titleCalculTpsConcentrationRetenuCol2 = tpsConcentrationRetenuRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculTpsConcentrationRetenuCol2, "Tc=");

		rowIndexExutoire++;

		Row intensiteAverseRow = sheet.createRow(rowIndexExutoire);
		Cell titleIntensiteAverse = intensiteAverseRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleIntensiteAverse, "Calcul de l'intensité de l'averse (mm/h)");
		Cell titleIntensiteAverseCol2 = intensiteAverseRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleIntensiteAverseCol2, "I(d,T)=");

		rowIndexExutoire++;

		Row calculDebitRow = sheet.createRow(rowIndexExutoire);
		Cell titleCalculDebit = calculDebitRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleCalculDebit, "Calcul du débit par la méthode rationnelle (m3/s)");
		Cell titleCalculDebitCol2 = calculDebitRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculDebitCol2, "Q pointe =");

		rowIndexExutoire++;

		// une ligne vide
//		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexExutoire, 0, TAILLE_LOT + 2);

		rowIndexExutoire++;

		Row title2Row = sheet.createRow(rowIndexExutoire);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, TAILLE_LOT + 2);
		title2Row.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Dimensionnement des sections des ouvrages de transit pour une crue centennale";
		Cell headerCell = title2Row.createCell(indexColumn);
		XlsUtils.title2(computeContext, headerCell, title);

		rowIndexExutoire++;

		Row lameEauRow = sheet.createRow(rowIndexExutoire);
		Cell lameEauCell = lameEauRow.createCell(indexColumn);
		title3(computeContext, lameEauCell, "Hauteur de lame d'eau");
		Cell lameEauCell2 = lameEauRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, lameEauCell2, "(m)");

		rowIndexExutoire++;

		Row revancheRow = sheet.createRow(rowIndexExutoire);
		Cell revancheCell = revancheRow.createCell(indexColumn);
		title3(computeContext, revancheCell, "Revanche (m)");
		Cell revancheCell2 = revancheRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, revancheCell2, "(m)");

		rowIndexExutoire++;

		Row evacuateurRow = sheet.createRow(rowIndexExutoire);
		Cell evacuateurCell = evacuateurRow.createCell(indexColumn);
		title3(computeContext, evacuateurCell, "L:  Largeur de l'évacuateur (m)");
		Cell evacuateurCell2 = evacuateurRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, evacuateurCell2, "L déversoir (m)");

		rowIndexExutoire++;

		Row seuilRow = sheet.createRow(rowIndexExutoire);
		Cell seuilCell = seuilRow.createCell(indexColumn);
		title3(computeContext, seuilCell, "H:  Hauteur de la charge sur le seuil (lame d'eau (m))");
		Cell seuilCell2 = seuilRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, seuilCell2, "H déversoir (m)");

		rowIndexExutoire++;
		rowIndexExutoire++;

		Row dimResumeRow = sheet.createRow(rowIndexExutoire);
		Cell dimResumeCell = dimResumeRow.createCell(indexColumn);
		title3(computeContext, dimResumeCell, "Dimensions de la zone de passage de l'eau (m) :");
		XlsUtils.mergeCol(computeContext, sheet, indexColumn, rowIndexExutoire, rowIndexExutoire + 1);

		Cell dimResumeLCell2 = dimResumeRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeLCell2, "Largeur (m)");
		rowIndexExutoire++;
		Row dimResumeHRow2 = sheet.createRow(rowIndexExutoire);
		Cell dimResumeHCell2 = dimResumeHRow2.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeHCell2, "Hauteur (m)");

		rowIndexExutoire++;

		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		for (Creek c : creeks) {

			if (c.getExutoires().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, creekRow.getRowNum(), indexColumn,
						indexColumn + c.exutoires.size() - 1);
			}

			Cell creekCell = creekRow.createCell(indexColumn);
			backBlueBoldBorderTopLeftRight(computeContext, creekCell, c.nom);

			List<Cell> cells = new ArrayList<>();

			for (Exutoire e : c.getExutoires()) {

				Cell exuNomCell = exutoireRow.createCell(indexColumn);
				redBoldBorderLeftRight(computeContext, exuNomCell, e.getNom());

				Cell exuSurfCell = superficieRow.createCell(indexColumn);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 10000);

				Cell lgHydroCell = longueurRow.createCell(indexColumn);
				standardCellDecimalNoComma(computeContext, lgHydroCell, "")
						.setCellValue(e.getLongueurHydro().intValue());

				Cell deniveleCell = deniveleRow.createCell(indexColumn);
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(e.getDenivele().intValue());

				Cell penteCell = penteRow.createCell(indexColumn);
				String penteFormula = String.format("INT((%s%s/%s%s)*100)",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimalNoComma(computeContext, penteCell, "").setCellFormula(penteFormula);

				Cell ruissellementCell = ruissellementRow.createCell(indexColumn);
				standardCell(computeContext, ruissellementCell, "")
						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));

				Cell ecoulementCell = ecoulementRow.createCell(indexColumn);
				String ecoulementFormula = String.format("IF(%s%s<10,\"1\", IF(%s%s>15, \"4\", \"2\"))",
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1);
				standardCell(computeContext, ecoulementCell, "").setCellFormula(ecoulementFormula);

				Cell calculTpsConcCell = calculTpsConcentrationRow.createCell(indexColumn);
				String calculTpsConcFormula = String.format("%s%s/%s%s/60",
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1,
						CellReference.convertNumToColString(ecoulementCell.getColumnIndex()),
						ecoulementCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcCell, "").setCellFormula(calculTpsConcFormula);

				Cell tpsConcRetenuCell = tpsConcentrationRetenuRow.createCell(indexColumn);
				String tpsConcRetenuFormula = String.format("IF(%s%s>6,%s%s, \"6\")",
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1);
				standardCell(computeContext, tpsConcRetenuCell, "").setCellFormula(tpsConcRetenuFormula);

				Cell calculAverseCell = intensiteAverseRow.createCell(indexColumn);
				String calculAverseFormula = String.format("%s*(%s%s^%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);

				Cell calculDebitCell = calculDebitRow.createCell(indexColumn);
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s*0.01)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);

				Cell calculHauteurLameEauCell = lameEauRow.createCell(indexColumn);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);

				Cell calculRevancheCell = revancheRow.createCell(indexColumn);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCellDecimal2Comma(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);

				Cell calculLargeurEvacuateurCell = evacuateurRow.createCell(indexColumn);
				String calculLargeurEvacuateurFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculLargeurEvacuateurCell, "")
						.setCellFormula(calculLargeurEvacuateurFormula);

				Cell calculHChargeSeuilCell = seuilRow.createCell(indexColumn);
				String calculHChargeSeuilFormula = String.format("%s%s/(%s*POWER(2*%s,0.5)*POWER(%s%s,3/2))",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_N_PARAM),
						parametresGenerator.parametres.get(ParametresGenerator.CST_G_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHChargeSeuilCell, "")
						.setCellFormula(calculHChargeSeuilFormula);

				Cell calculDimResumeLCell = dimResumeRow.createCell(indexColumn);
				String calculDimResumeLFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculLargeurEvacuateurCell.getColumnIndex()),
						calculLargeurEvacuateurCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);

				Cell calculDimResumeHCell = dimResumeHRow2.createCell(indexColumn);
				String calculDimResumeHFormula = String.format("MROUND(%s%s+0.25,0.5)",
						CellReference.convertNumToColString(calculHChargeSeuilCell.getColumnIndex()),
						calculHChargeSeuilCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);

				indexColumn++;
			}

		}

//		XlsUtils.makeBoldBorder(sheet, firstRow+14, firstRow+15, 1, indexColumn-1);
//		
//		XlsUtils.makeBoldBorder(sheet, firstRow+14, firstRow+15, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, firstRow+1, rowIndexExutoire-1, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, firstRow, firstRow+1, 3, indexColumn-1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow+1, rowIndexExutoire-1, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexExutoire-7, rowIndexExutoire-6, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexExutoire-2, rowIndexExutoire, 1, indexColumn-1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 3, firstRow + 13, 1, 2);
		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 22, 1, 2);

		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 13, 1, indexColumn - 1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 15, 1, indexColumn - 1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, 2);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 3, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 1, 3, indexColumn - 1);

//		XlsUtils.makeBoldBorder(sheet, firstRow - 1, firstRow + 22, 1, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow - 1, firstRow, 3, indexColumn - 1);

	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		// une colonne vide
		int indexColumn = 1;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Caractéristiques des bassins versants d'exutoires et débits associés à l'état initial ";
		Cell headerCell = titleRow.createCell(1);
		title1(computeContext, headerCell, title).setCellFormula("CONCATENATE(\"" + title + "\","
				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexExutoire++;
	}

	private void generateLegend() {

		// une colonne vide
		int indexColumn = 3;

		rowIndexExutoire++;
		rowIndexExutoire++;

		Row legend1Row = sheet.createRow(rowIndexExutoire);
		Cell legend1Color = legend1Row.createCell(indexColumn);
		Cell legend1Text = legend1Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend1Color, IndexedColors.LIME);
		standardCell(computeContext, legend1Text, "exutoire stable (talweg végétalisé/fond rocheux)");

		rowIndexExutoire++;

		Row legend2Row = sheet.createRow(rowIndexExutoire);
		Cell legend2Color = legend2Row.createCell(indexColumn);
		Cell legend2Text = legend2Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend2Color, IndexedColors.GOLD);
		standardCell(computeContext, legend2Text, "exutoire stable pour le débit reçu mais à ne pas suralimenter");

		rowIndexExutoire++;

		Row legend3Row = sheet.createRow(rowIndexExutoire);
		Cell legend3Color = legend3Row.createCell(indexColumn);
		Cell legend3Text = legend3Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend3Color, IndexedColors.ORANGE);
		standardCell(computeContext, legend3Text, "exutoire instable à fort risque d'érosion regressive");

		rowIndexExutoire++;

	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
