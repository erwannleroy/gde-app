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
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
public class Q100Generator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Q100";
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	ParametresGenerator parametresGenerator;

	@Autowired
	TCGenerator tcGenerator;

	public void doRun() throws GDEException {
		log.info("Génération de l'onglet Exutoire");

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
		sheet.setColumnWidth(2, 11 * 256);
		sheet.setColumnWidth(3, 12 * 256);
		sheet.setColumnWidth(4, 10 * 256);
		sheet.setColumnWidth(5, 8 * 256);
		sheet.setColumnWidth(6, 12 * 256);
		sheet.setColumnWidth(7, 12 * 256);
		sheet.setColumnWidth(8, 12* 256);
		sheet.setColumnWidth(9, 12 * 256);
		sheet.setColumnWidth(10, 1 * 256);
		sheet.setColumnWidth(11, 12 * 256);
		sheet.setColumnWidth(12, 1 * 256);
		sheet.setColumnWidth(13, 12 * 256);
		sheet.setColumnWidth(14, 10 * 256);
		sheet.setColumnWidth(15, 10 * 256);
		sheet.setColumnWidth(16, 12 * 256);
		sheet.setColumnWidth(17, 1 * 256);
		sheet.setColumnWidth(18, 12 * 256);
		sheet.setColumnWidth(19, 9 * 256);

		XlsUtils.borderMergedSheet(sheet);
		
		sheet.setRepeatingRows(new CellRangeAddress(2, 3, 0, 0));
		sheet.createFreezePane(0, 4);

		notifyListeners(SheetGeneratorEvent.Q100_SHEET_GENERATED, null);
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
		title2(computeContext, caracExuTitleCell, "Caractéristiques des bassins versants associés aux exutoires");

		Cell debitTitleCell = enteteRow.createCell(11);
		title2(computeContext, debitTitleCell, "Q100");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 13, 16);
		Cell crueTitleCell = enteteRow.createCell(13);
		title2(computeContext, crueTitleCell, "Dimensionnement des déversoirs");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 18, 19);
		Cell dimTitleCell = enteteRow.createCell(18);
		title2(computeContext, dimTitleCell, "Section des déversoirs");

		rowIndexExutoire++;

		// entete tableau
		Row columnRow = sheet.createRow(rowIndexExutoire);

		int idxEntete=0;
		Cell creekCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, creekCellTitle, "Creek");

		Cell exuCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, exuCellTitle, "Exutoire");

		Cell supCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, supCellTitle, "Superficie BV (ha)");

		Cell longueurCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, longueurCellTitle, "Longueur hydraulique BV (m)");

		Cell denivCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, denivCellTitle, "Dénivelé BV (m)");

		Cell penteCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, penteCellTitle, "Pente BV (%)");

		Cell coefRuisCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, coefRuisCellTitle, "Coefficient de ruissellement");

		Cell vitEcoulCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, vitEcoulCellTitle, "Vitesse d'écoulement (m/s)");

		Cell tpsConcCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, tpsConcCellTitle, "Temps de concentration retenu (mn)");

		Cell intAvObjCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, intAvObjCellTitle, "Calcul de l'intensité de l'averse (mm/h)");

		// une séparation
		idxEntete++;

		Cell debitCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, debitCellTitle, "Calcul du débit par la méthode rationnelle (m3/s)");

		// une séparation
		idxEntete++;

		Cell hauteurLameCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, hauteurLameCellTitle, "Hauteur de lame d'eau (m)");

		Cell revancheCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, revancheCellTitle, "Revanche (m)");

		Cell largEvacCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, largEvacCellTitle, "Largeur de l'évacuateur (m)");

		Cell hauteurChargeCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, hauteurChargeCellTitle, "Hauteur de la charge sur le seuil (lame d'eau (m))");

		// une séparation
		idxEntete++;

		Cell largeurCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, largeurCellTitle, "Largeur (m)");

		Cell hauteurCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, hauteurCellTitle, "Hauteur (m)");

		rowIndexExutoire++;

	}

	private void generateCreeks(List<Creek> creeks) throws GDEException {

		for (Creek c : creeks) {
			nbOuvragesTotal += c.exutoires.size();
		}
		
		log.info("Génération d'un lot de creeks, génération des autres colonnes");
		for (Creek c : creeks) {

			Row creekRow = sheet.createRow(rowIndexExutoire);

			if (c.exutoires.size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 0, rowIndexExutoire,
						rowIndexExutoire + c.exutoires.size() - 1);
			}
			Cell creekTitleCell = creekRow.createCell(0);
			redBoldVATop(computeContext, creekTitleCell, c.nom);

			Row exuRow = creekRow;

			for (BVExutoire e : c.getExutoires()) {

				int idxRow = 1;

				Cell exuNomCell = exuRow.createCell(idxRow++);
				redBold(computeContext, exuNomCell, e.getNom());

				Cell exuSurfCell = exuRow.createCell(idxRow++);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 10000);

				Cell lgHydroCell = exuRow.createCell(idxRow++);
				standardCellDecimalNoComma(computeContext, lgHydroCell, "")
						.setCellValue(e.getLongueurHydro().intValue());

				Cell deniveleCell = exuRow.createCell(idxRow++);
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(e.getDenivele().intValue());

				Cell penteCell = exuRow.createCell(idxRow++);
				String penteFormula = String.format("(%s%s/%s%s)*100",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimalNoComma(computeContext, penteCell, "").setCellFormula(penteFormula);

				Cell ruissellementCell = exuRow.createCell(idxRow++);
				standardCell(computeContext, ruissellementCell, "")
						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));

				Cell ecoulementCell = exuRow.createCell(idxRow++);
				String ecoulementFormula = String.format("IF(%s%s<5,\"1\", IF(%s%s>15, \"4\", \"2\"))",
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1);
				standardCell(computeContext, ecoulementCell, "").setCellFormula(ecoulementFormula);

				Cell tpsConcRetenuCell = exuRow.createCell(idxRow++);
				standardCellDecimal2Comma(computeContext, tpsConcRetenuCell, "").setCellFormula(tcGenerator.getReferenceTC(e.getNom()));

				Cell calculAverseCell = exuRow.createCell(idxRow++);
				String calculAverseFormula = String.format("%s*(%s%s^-%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);

				// une séparation
				idxRow++;

				Cell calculDebitCell = exuRow.createCell(idxRow++);
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s*0.01)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal1Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);

				FormulaEvaluator evaluator = workbook().getCreationHelper().createFormulaEvaluator();

				// existing Sheet, Row, and Cell setup
				evaluator.evaluateFormulaCell(calculDebitCell);
				
				double calculDebitCellValue = 0;
				try {
					calculDebitCellValue = calculDebitCell.getNumericCellValue();
				} catch (Exception fe) {
					//super.processFormulaError(calculDebitCell);
					//break;
				}


				log.info("Debit bv " + e.getNom()+ " : " + calculDebitCellValue);
				this.computeContext.getDebitBVExutoire().put(e.getNom(), calculDebitCellValue);

				// une séparation
				idxRow++;

				Cell calculHauteurLameEauCell = exuRow.createCell(idxRow++);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);

				Cell calculRevancheCell = exuRow.createCell(idxRow++);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCellDecimal2Comma(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);

				Cell calculLargeurEvacuateurCell = exuRow.createCell(idxRow++);
				String calculLargeurEvacuateurFormula = String.format("%s%s/(%s*POWER(2*%s,0.5)*POWER(%s%s,3/2))",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_N_PARAM),
						parametresGenerator.parametres.get(ParametresGenerator.CST_G_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculLargeurEvacuateurCell, "")
						.setCellFormula(calculLargeurEvacuateurFormula);

				Cell calculHChargeSeuilCell = exuRow.createCell(idxRow++);
				String calculHChargeSeuilFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHChargeSeuilCell, "")
						.setCellFormula(calculHChargeSeuilFormula);

				// une séparation
				idxRow++;

				Cell calculDimResumeLCell = exuRow.createCell(idxRow++);
				String calculDimResumeLFormula = String.format("MROUND(%s%s+0.25,0.5)",
						CellReference.convertNumToColString(calculLargeurEvacuateurCell.getColumnIndex()),
						calculLargeurEvacuateurCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);

				Cell calculDimResumeHCell = exuRow.createCell(idxRow++);
				String calculDimResumeHFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculHChargeSeuilCell.getColumnIndex()),
						calculHChargeSeuilCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.Q100_SHEET_PROGRESS, (int) progress);

				rowIndexExutoire++;

				exuRow = sheet.createRow(rowIndexExutoire);
			}

			short spaceBV = 5;
			exuRow.setHeightInPoints(spaceBV);

			rowIndexExutoire++;
		}

	}
	
	@Override
	protected List<String> getListErrors(ComputingResult cr) {
		return cr.getQ100Warns();
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		short htitle = 20;
		titleRow.setHeightInPoints(htitle);

		XlsUtils.mergeRow(computeContext, sheet, 0, 0, 19);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Débits centennaux aux exutoires et dimensionnement des déversoirs";
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
		computeContext.getComputingResult().setQ100ComputeProgress(0);
		computeContext.getComputingResult().setQ100ComputeOk(false);
	}
}
