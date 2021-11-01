package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
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
import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CassisGenerator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Dimensionnement cassis fossés";
	private static final int TAILLE_LOT = 15;

	@Autowired
	ParametresGenerator parametresGenerator;

	@Override
	protected void startGeneration() {
		log.info("Génération de l'onglet Cassis");
		
		sheet = workbook().getSheet(TITLE_SHEET);
		
		if (null != sheet) {
			workbook().removeSheetAt(4);
		} 

		sheet = workbook().createSheet(TITLE_SHEET);
		
		sheet.setColumnWidth(0, 2);

		rowIndexExutoire = 0;

		generateTitleBlock();

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

		int nbOuvrage = 0;
		for(Creek c : creeks) {
			nbOuvrage += c.exutoires.size();
		}
		
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
		title3LeftBorder(computeContext, titleSuperficie, "Superficie BV (km²) :");
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
		Row blankRow = sheet.createRow(rowIndexExutoire);
		blankRow.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT/2));

		rowIndexExutoire++;

		Row title2Row = sheet.createRow(rowIndexExutoire);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, nbOuvrage + 2);
		title2Row.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Sections des fossés et cassis";
		Cell headerCell = title2Row.createCell(indexColumn);
		XlsUtils.title2(computeContext, headerCell, title);

		rowIndexExutoire++;

		Row desc2Row = sheet.createRow(rowIndexExutoire);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, nbOuvrage + 2);
		desc2Row.setRowStyle(XlsUtils.blankRow(computeContext));
		String description = "Le cassis est assimilé à un fossé rectangulaire.\nApproximation par section rectangulaire et formules de Manning-Strickler / Chezy   (comparaison des 2 membres de la formule)";
		Cell descCell = desc2Row.createCell(indexColumn);
		XlsUtils.subTitleCell(computeContext, descCell, description);

		rowIndexExutoire++;

		Row penteFosseRow = sheet.createRow(rowIndexExutoire);
		Cell penteFosseCell = penteFosseRow.createCell(indexColumn);
		title3(computeContext, penteFosseCell, "Pente fossé-cassis (m/m)");
//		Cell lameEauCell2 = lameEauRow.createCell(indexColumn + 1);
//		title3RightBorder(computeContext, lameEauCell2, "(m)");

		rowIndexExutoire++;

		Row coefStricklerRow = sheet.createRow(rowIndexExutoire);
		Cell coefStricklerCell = coefStricklerRow.createCell(indexColumn);
		title3(computeContext, coefStricklerCell, "Coef. rugosié Strickler (Ks) lié à fossé-cassis");
//		Cell revancheCell2 = coefStricklerRow.createCell(indexColumn + 1);
//		title3RightBorder(computeContext, revancheCell2, "(m)");

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

		Row largeurFosseRow = sheet.createRow(rowIndexExutoire);
		Cell largeurFosseCell = largeurFosseRow.createCell(indexColumn);
		title3(computeContext, largeurFosseCell, "L:  Largeur du fossé-cassis (m)");
//		Cell evacuateurCell2 = evacuateurRow.createCell(indexColumn + 1);
//		title3RightBorder(computeContext, evacuateurCell2, "L déversoir (m)");

		rowIndexExutoire++;

		Row hauteurFosseRow = sheet.createRow(rowIndexExutoire);
		Cell hauteurFosseCell = hauteurFosseRow.createCell(indexColumn);
		title3(computeContext, hauteurFosseCell, "H:  Hauteur du fossé-cassis");
//		Cell seuilCell2 = hauteurFosseRow.createCell(indexColumn + 1);
//		title3RightBorder(computeContext, seuilCell2, "H déversoir (m)");

		rowIndexExutoire++;
		rowIndexExutoire++;

		Row premierMembreRow = sheet.createRow(rowIndexExutoire);
		Cell premierMembreCell = premierMembreRow.createCell(indexColumn);
		title3(computeContext, premierMembreCell, "Valeur du 1er membre :  (Qp/(Ks*Pmoy0.5))3/2");

		rowIndexExutoire++;

		Row deuxiemeMembreRow = sheet.createRow(rowIndexExutoire);
		Cell deuxiemeMembreCell = deuxiemeMembreRow.createCell(indexColumn);
		title3(computeContext, deuxiemeMembreCell, "Valeur du 2ème membre :  (yL)5/2/(2y+L)");

		rowIndexExutoire++;

		Row dimResumeRow = sheet.createRow(rowIndexExutoire);
		Cell dimResumeCell = dimResumeRow.createCell(indexColumn);
		title3(computeContext, dimResumeCell, "Dimensions retenues");
		XlsUtils.mergeCol(computeContext, sheet, indexColumn, rowIndexExutoire, rowIndexExutoire + 1);

		Cell dimResumeLCell2 = dimResumeRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeLCell2, "Largeur (m)");
		rowIndexExutoire++;
		Row dimResumeHRow2 = sheet.createRow(rowIndexExutoire);
		Cell dimResumeHCell2 = dimResumeHRow2.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeHCell2, "Hauteur (m)");

		rowIndexExutoire++;

		Row vitMaxRow = sheet.createRow(rowIndexExutoire);
		Cell vitMaxCell = vitMaxRow.createCell(indexColumn);
		title3(computeContext, vitMaxCell, "Vitesse max. dans fossé-cassis (m/s)");
		Cell vitMaxCol2 = vitMaxRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, vitMaxCol2, "V max (m/s)");

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

			for (BVExutoire e : c.getExutoires()) {

				Cell exuNomCell = exutoireRow.createCell(indexColumn);
				redBoldBorderLeftRight(computeContext, exuNomCell, e.getNom());

				Cell exuSurfCell = superficieRow.createCell(indexColumn);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 1000000);

				Cell lgHydroCell = longueurRow.createCell(indexColumn);
				standardCell(computeContext, lgHydroCell, "").setCellValue(e.getLongueurHydro().intValue());

				Cell deniveleCell = deniveleRow.createCell(indexColumn);
				standardCell(computeContext, deniveleCell, "").setCellValue(e.getDenivele());

				Cell penteCell = penteRow.createCell(indexColumn);
				String penteFormula = String.format("(%s%s/%s%s)*100",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, penteCell, "").setCellFormula(penteFormula);

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
				standardCellDecimal2Comma(computeContext, tpsConcRetenuCell, "").setCellFormula(tpsConcRetenuFormula);

				Cell calculAverseCell = intensiteAverseRow.createCell(indexColumn);
				String calculAverseFormula = String.format("%s*(%s%s^%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);

				Cell calculDebitCell = calculDebitRow.createCell(indexColumn);
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);

				Cell calculPenteFosseCell = penteFosseRow.createCell(indexColumn);
				String penteFosseFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.CST_PENTE_PARAM));
				standardCell(computeContext, calculPenteFosseCell, "").setCellFormula(penteFosseFormula);

				Cell calculHauteurLameEauCell = lameEauRow.createCell(indexColumn);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);

				Cell calculRevancheCell = revancheRow.createCell(indexColumn);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCell(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);

				Cell calculLargeurFosseCell = largeurFosseRow.createCell(indexColumn);
				String calculLargeurFosseFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculLargeurFosseCell, "")
						.setCellFormula(calculLargeurFosseFormula);

				Cell calculHauteurFosseCell = hauteurFosseRow.createCell(indexColumn);

				Cell calculPremierMembreCell = premierMembreRow.createCell(indexColumn);
				String calculPremierMembreFormula = String.format("POWER(%s%s/(%s*POWER(%s%s,1/2)),3/2)",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEF_STRICKLER_PARAM),
						CellReference.convertNumToColString(calculPenteFosseCell.getColumnIndex()),
						calculPenteFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculPremierMembreCell, "")
						.setCellFormula(calculPremierMembreFormula);

				Cell calculDeuxiemeMembreCell = deuxiemeMembreRow.createCell(indexColumn);
				String calculDeuxiemeMembreFormula = String.format("(POWER(%s%s*%s%s,5/2))/(2*%s%s+%s%s)",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDeuxiemeMembreCell, "")
						.setCellFormula(calculDeuxiemeMembreFormula);

				Cell calculDimResumeLCell = dimResumeRow.createCell(indexColumn);
				String calculDimResumeLFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);

				Cell calculDimResumeHCell = dimResumeHRow2.createCell(indexColumn);
				String calculDimResumeHFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculHauteurFosseCell.getColumnIndex()),
						calculHauteurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);

				Cell calculVitMaxCell = vitMaxRow.createCell(indexColumn);
				String calculVitMaxFormula = String.format("%s*POWER((%s%s*%s%s)/(2*%s%s+%s%s),2/3)*POWER(%s%s,1/2)",
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEF_STRICKLER_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculPenteFosseCell.getColumnIndex()),
						calculPenteFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculVitMaxCell, "").setCellFormula(calculVitMaxFormula);

				indexColumn++;

			}

		}

		XlsUtils.makeBoldBorder(sheet, firstRow + 3, firstRow + 13, 1, 2);
		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 22, 1, 2);

		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 27, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 13, 1, indexColumn - 1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 15, 1, indexColumn - 1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, 2);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 3, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 1, 3, indexColumn - 1);

		XlsUtils.makeBoldBorder(sheet, firstRow + 14, firstRow + 15, 1, indexColumn - 1);
		XlsUtils.makeBoldBorder(sheet, firstRow + 1, rowIndexExutoire - 1, 1, indexColumn - 1);
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		// une colonne vide
		int indexColumn = 1;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Dimensionnement des ouvrages de canalisation à créer sur le site ";
		Cell headerCell = titleRow.createCell(1);
		title1(computeContext, headerCell, title);
//		.setCellFormula("CONCATENATE(\"" + title + "\","
//				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexExutoire++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
