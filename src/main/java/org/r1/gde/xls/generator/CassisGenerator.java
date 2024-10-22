package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.redBold;
import static org.r1.gde.XlsUtils.redBoldVATop;
import static org.r1.gde.XlsUtils.standardCell;
import static org.r1.gde.XlsUtils.standardCellDecimal1Comma;
import static org.r1.gde.XlsUtils.standardCellDecimal2Comma;
import static org.r1.gde.XlsUtils.standardCellDecimalNoComma;
import static org.r1.gde.XlsUtils.title1;
import static org.r1.gde.XlsUtils.title2;
import static org.r1.gde.XlsUtils.title3;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.Creek;
import org.r1.gde.service.ComputingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CassisGenerator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Dimensionnement cassis fossés";
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	TCGenerator tcGenerator;

	@Autowired
	ParametresGenerator parametresGenerator;

	public void doRun() throws GDEException {
		log.info("Génération de l'onglet Cassis");

		sheet = workbook().getSheet(TITLE_SHEET);

		if (null != sheet) {
			workbook().removeSheetAt(workbook().getSheetIndex(sheet));
		}

		sheet = workbook().createSheet(TITLE_SHEET);
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
		sheet.setMargin(Sheet.RightMargin, 0.4);
		sheet.setMargin(Sheet.LeftMargin, 0.4);
		sheet.setMargin(Sheet.TopMargin, 0.4);
		sheet.setMargin(Sheet.BottomMargin, 0.4);

		rowIndexExutoire = 0;

		generateTitleBlock();

		generateEntete();
		
		if (creeks() != null) {
			generateCreeks(creeks());
		}
		
		sheet.setColumnWidth(0, 12 * 256);
		sheet.setColumnWidth(1, 12 * 256);
		sheet.setColumnWidth(2, 10 * 256);
		sheet.setColumnWidth(3, 11 * 256);
		sheet.setColumnWidth(4, 8 * 256);
		sheet.setColumnWidth(5, 8 * 256);
		sheet.setColumnWidth(6, 8 * 256);
		sheet.setColumnWidth(7, 8 * 256);
		sheet.setColumnWidth(8, 10 * 256);
		sheet.setColumnWidth(9, 10 * 256);
		sheet.setColumnWidth(10, 1 * 256);
		sheet.setColumnWidth(11, 10 * 256);
		sheet.setColumnWidth(12, 1 * 256);
		sheet.setColumnWidth(13, 8 * 256);
		sheet.setColumnWidth(14, 7 * 256);
		sheet.setColumnWidth(15, 8 * 256);
		sheet.setColumnWidth(16, 9 * 256);
		sheet.setColumnWidth(17, 8 * 256);
		sheet.setColumnWidth(18, 8 * 256);
		sheet.setColumnWidth(19, 8 * 256);
		sheet.setColumnWidth(20, 1 * 256);
		sheet.setColumnWidth(21, 8 * 256);
		sheet.setColumnWidth(22, 7 * 256);
		sheet.setColumnWidth(23, 7 * 256);

		XlsUtils.borderMergedSheet(sheet);

		sheet.createFreezePane(0, 4);
		sheet.setRepeatingRows(new CellRangeAddress(2, 3, 0, 0));
		
		notifyListeners(SheetGeneratorEvent.CASSIS_SHEET_GENERATED, null);
	}

	private void generateEntete() {
		// une ligne vide
		rowIndexExutoire++;

		// enteteGroup
		Row enteteRow = sheet.createRow(rowIndexExutoire);
		short htitle = 35;
		enteteRow.setHeightInPoints(htitle);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 0, 9);
		Cell caracExuTitleCell = enteteRow.createCell(0);
		title2(computeContext, caracExuTitleCell, "Caractéristiques des BV associés aux exutoires");

//		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 9, 12);
		Cell debitTitleCell = enteteRow.createCell(11);
		title2(computeContext, debitTitleCell, "Q100");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 13, 19);
		Cell fosCasTitleCell = enteteRow.createCell(13);
		title2(computeContext, fosCasTitleCell, "Dimensionnement des fossé-cassis");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 21, 23);
		Cell dimTitleCell = enteteRow.createCell(21);
		title2(computeContext, dimTitleCell, "Section des ouvrages");

		rowIndexExutoire++;

		// entete tableau
		Row columnRow = sheet.createRow(rowIndexExutoire);

		Cell creekCellTitle = columnRow.createCell(0);
		title3(computeContext, creekCellTitle, "Creek");

		Cell exuCellTitle = columnRow.createCell(1);
		title3(computeContext, exuCellTitle, "Exutoire");

		Cell supCellTitle = columnRow.createCell(2);
		title3(computeContext, supCellTitle, "Superficie BV (km²)");

		Cell longueurCellTitle = columnRow.createCell(3);
		title3(computeContext, longueurCellTitle, "Longueur hydraulique BV (m)");

		Cell denivCellTitle = columnRow.createCell(4);
		title3(computeContext, denivCellTitle, "Dénivelé BV (m)");

		Cell penteCellTitle = columnRow.createCell(5);
		title3(computeContext, penteCellTitle, "Pente BV (%)");

		Cell coefRuisCellTitle = columnRow.createCell(6);
		title3(computeContext, coefRuisCellTitle, "Coefficient de ruissellement");

		Cell vitEcoulCellTitle = columnRow.createCell(7);
		title3(computeContext, vitEcoulCellTitle, "Vitesse d'écoulement (m/s)");

		Cell tpsConcCellTitle = columnRow.createCell(8);
		title3(computeContext, tpsConcCellTitle, "Temps de concentration retenu (mn)");

		Cell intAvObjCellTitle = columnRow.createCell(9);
		title3(computeContext, intAvObjCellTitle, "Calcul de l'intensité de l'averse (mm/h)");

		Cell debitCellTitle = columnRow.createCell(11);
		title3(computeContext, debitCellTitle, "Calcul du débit par la méthode rationnelle (m3/s)");

		Cell penteFosCasCellTitle = columnRow.createCell(13);
		title3(computeContext, penteFosCasCellTitle, "Pente fossé-cassis (m/m)");

		Cell hauteurLameCellTitle = columnRow.createCell(14);
		title3(computeContext, hauteurLameCellTitle, "Hauteur de lame d'eau (m)");

		Cell revancheCellTitle = columnRow.createCell(15);
		title3(computeContext, revancheCellTitle, "Revanche (m)");
		
		Cell largFosCasCellTitle = columnRow.createCell(16);
		title3(computeContext, largFosCasCellTitle, "Largeur du fossé-cassis (m)");

		Cell hauteurFosCasCellTitle = columnRow.createCell(17);
		title3(computeContext, hauteurFosCasCellTitle, "Hauteur du fossé-cassis (m)");

		Cell val1erCellTitle = columnRow.createCell(18);
		title3(computeContext, val1erCellTitle, "Valeur du 1er membre");

		Cell val2emeCellTitle = columnRow.createCell(19);
		title3(computeContext, val2emeCellTitle, "Valeur du 2ème membre");

		Cell largeurCellTitle = columnRow.createCell(21);
		title3(computeContext, largeurCellTitle, "Largeur (m)");

		Cell hauteurCellTitle = columnRow.createCell(22);
		title3(computeContext, hauteurCellTitle, "Hauteur (m)");
		
		Cell vitMaxCellTitle = columnRow.createCell(23);
		title3(computeContext, vitMaxCellTitle, "Vitesse max dans fossé-cassis (m/s)");

		rowIndexExutoire++;
	}

	private void generateCreeks(List<Creek> creeks) throws GDEException {

		
		for (Creek c : creeks) {
			nbOuvragesTotal += c.exutoires.size();
		}
		
		log.info("Génération des creeks - debut");
		for (Creek c : creeks) {

			Row creekRow = sheet.createRow(rowIndexExutoire);

			if (c.exutoires.size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 0, rowIndexExutoire,
						rowIndexExutoire + c.exutoires.size() - 1);
			}
			Cell creekTitleCell = creekRow.createCell(0);
			redBoldVATop(computeContext, creekTitleCell, c.nom);

			Row exuRow = creekRow;


			log.info("Génération des exutoires - debut");
			for (BVExutoire e : c.getExutoires()) {

				Cell exuNomCell = exuRow.createCell(1);
				redBold(computeContext, exuNomCell, e.getNom());

				Cell exuSurfCell = exuRow.createCell(2);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 1000000);

				Cell lgHydroCell = exuRow.createCell(3);
				standardCell(computeContext, lgHydroCell, "").setCellValue(e.getLongueurHydro().intValue());

				Cell deniveleCell = exuRow.createCell(4);
				standardCell(computeContext, deniveleCell, "").setCellValue(e.getDenivele());

				Cell penteCell = exuRow.createCell(5);
				String penteFormula = String.format("(%s%s/%s%s)*100",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimalNoComma(computeContext, penteCell, "").setCellFormula(penteFormula);

				Cell ruissellementCell = exuRow.createCell(6);
				standardCell(computeContext, ruissellementCell, "")
						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));

				Cell ecoulementCell = exuRow.createCell(7);
				String ecoulementFormula = String.format("IF(%s%s<5,\"1\", IF(%s%s>15, \"4\", \"2\"))",
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1);
				standardCell(computeContext, ecoulementCell, "").setCellFormula(ecoulementFormula);

				Cell tpsConcRetenuCell = exuRow.createCell(8);
				standardCellDecimal2Comma(computeContext, tpsConcRetenuCell, "").setCellFormula(tcGenerator.getReferenceTC(e.getNom()));

				Cell calculAverseCell = exuRow.createCell(9);
				String calculAverseFormula = String.format("%s*(%s%s^-%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);

				Cell calculDebitCell = exuRow.createCell(11);
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal1Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);

				Cell calculPenteFosseCell = exuRow.createCell(13);
				String penteFosseFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.CST_PENTE_PARAM));
				standardCell(computeContext, calculPenteFosseCell, "").setCellFormula(penteFosseFormula);

				Cell calculHauteurLameEauCell = exuRow.createCell(14);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);

				Cell calculRevancheCell = exuRow.createCell(15);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCell(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);

				Cell calculLargeurFosseCell = exuRow.createCell(16);
				standardCellDecimal2Comma(computeContext, calculLargeurFosseCell, "0.00").setCellValue(0d);

				Cell calculHauteurFosseCell = exuRow.createCell(17);
				String calculHauteurFosseFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHauteurFosseCell, "")
						.setCellFormula(calculHauteurFosseFormula);

				Cell calculPremierMembreCell = exuRow.createCell(18);
				String calculPremierMembreFormula = String.format("POWER(%s%s/(%s*POWER(%s%s,1/2)),3/2)",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEF_STRICKLER_PARAM),
						CellReference.convertNumToColString(calculPenteFosseCell.getColumnIndex()),
						calculPenteFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculPremierMembreCell, "")
						.setCellFormula(calculPremierMembreFormula);

				Cell calculDeuxiemeMembreCell = exuRow.createCell(19);
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

				Cell calculDimResumeLCell = exuRow.createCell(21);
				String calculDimResumeLFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);

				Cell calculDimResumeHCell = exuRow.createCell(22);
				String calculDimResumeHFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculHauteurFosseCell.getColumnIndex()),
						calculHauteurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);

				Cell calculVitMaxCell = exuRow.createCell(23);
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

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.CASSIS_SHEET_PROGRESS, (int) progress);
				rowIndexExutoire++;

				log.info("Génération du creek " + c.nom + " - fin");

				exuRow = sheet.createRow(rowIndexExutoire);
			}

			short spaceBV = 5;
			exuRow.setHeightInPoints(spaceBV);

			rowIndexExutoire++;
			log.info("Génération des exutoires - fin");
		}

		log.info("Génération des creeks - fin");
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		short htitle = 20;
		titleRow.setHeightInPoints(htitle);
		XlsUtils.mergeRow(computeContext, sheet, 0, 0, 23);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Dimensionnement des ouvrages de canalisation à créer sur le site ";
		Cell headerCell = titleRow.createCell(0);
		title1(computeContext, headerCell, title);

		rowIndexExutoire++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

	@Override
	protected void detailError() {
		computeContext.getComputingResult().setCassisComputeProgress(0);
		computeContext.getComputingResult().setCassisComputeOk(false);
	}

	@Override
	protected List<String> getListErrors(ComputingResult cr) {
		return cr.getCassisWarns();
	}

}
