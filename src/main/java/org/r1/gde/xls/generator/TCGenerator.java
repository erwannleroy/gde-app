package org.r1.gde.xls.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.Creek;
import org.r1.gde.service.ComputingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.r1.gde.XlsUtils.*;

@Component
@Slf4j
public class TCGenerator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Q100";
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	ParametresGenerator parametresGenerator;

	public void doRun() {
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
		sheet.setColumnWidth(7, 10 * 256);
		sheet.setColumnWidth(8, 12* 256);
		sheet.setColumnWidth(9, 12 * 256);
		sheet.setColumnWidth(10, 12 * 256);
		sheet.setColumnWidth(11, 1 * 256);
		sheet.setColumnWidth(12, 12 * 256);
		sheet.setColumnWidth(13, 1 * 256);
		sheet.setColumnWidth(14, 10 * 256);
		sheet.setColumnWidth(15, 10 * 256);
		sheet.setColumnWidth(16, 12 * 256);
		sheet.setColumnWidth(17, 12 * 256);
		sheet.setColumnWidth(18, 1 * 256);
		sheet.setColumnWidth(19, 9 * 256);
		sheet.setColumnWidth(20, 9 * 256);

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

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 0, 10);
		Cell caracExuTitleCell = enteteRow.createCell(0);
		title2(computeContext, caracExuTitleCell, "Caractéristiques des bassins versants associés aux exutoires");

		Cell debitTitleCell = enteteRow.createCell(12);
		title2(computeContext, debitTitleCell, "Q100");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 14, 17);
		Cell crueTitleCell = enteteRow.createCell(14);
		title2(computeContext, crueTitleCell, "Dimensionnement des déversoirs");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, 19, 20);
		Cell dimTitleCell = enteteRow.createCell(19);
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

		Cell calcaulTpsConcCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, calcaulTpsConcCellTitle, "Calcul du temps de concentration (mn) - Formule normale");

//		Cell calcaulTpsConcCellTitle = columnRow.createCell(idxEntete++);
//		title3(computeContext, calcaulTpsConcCellTitle, "Calcul du temps de concentration (mn) - Formule DAVAR");
//
//		Cell calcaulTpsConcCellTitle = columnRow.createCell(idxEntete++);
//		title3(computeContext, calcaulTpsConcCellTitle, "Calcul du temps de concentration (mn) - Formule Meunier-Mathys");

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

	private void generateCreeks(List<Creek> creeks) {

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

				Cell exuNomCell = exuRow.createCell(1);
				redBold(computeContext, exuNomCell, e.getNom());

				Cell exuSurfCell = exuRow.createCell(2);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 10000);

				Cell lgHydroCell = exuRow.createCell(3);
				standardCellDecimalNoComma(computeContext, lgHydroCell, "")
						.setCellValue(e.getLongueurHydro().intValue());

				Cell deniveleCell = exuRow.createCell(4);
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(e.getDenivele().intValue());

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

				Cell calculTpsConcCell = exuRow.createCell(8);
				String calculTpsConcFormula = String.format("%s%s/%s%s/60",
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1,
						CellReference.convertNumToColString(ecoulementCell.getColumnIndex()),
						ecoulementCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcCell, "").setCellFormula(calculTpsConcFormula);

				Cell tpsConcRetenuCell = exuRow.createCell(9);
				String tpsConcRetenuFormula = String.format("IF(%s%s>%s,%s%s, %s)",
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM),
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM));
				standardCellDecimal2Comma(computeContext, tpsConcRetenuCell, "").setCellFormula(tpsConcRetenuFormula);

				Cell calculAverseCell = exuRow.createCell(10);
				String calculAverseFormula = String.format("%s*(%s%s^-%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);

				Cell calculDebitCell = exuRow.createCell(12);
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
					super.processFormulaError(calculDebitCell);
					break;
				}
				
				log.info("Debit bv " + e.getNom()+ " : " + calculDebitCellValue);
				this.computeContext.getDebitBVExutoire().put(e.getNom(), calculDebitCellValue);
				
				Cell calculHauteurLameEauCell = exuRow.createCell(14);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);

				Cell calculRevancheCell = exuRow.createCell(15);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCellDecimal2Comma(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);

				Cell calculLargeurEvacuateurCell = exuRow.createCell(16);
				String calculLargeurEvacuateurFormula = String.format("%s%s/(%s*POWER(2*%s,0.5)*POWER(%s%s,3/2))",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_N_PARAM),
						parametresGenerator.parametres.get(ParametresGenerator.CST_G_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculLargeurEvacuateurCell, "")
						.setCellFormula(calculLargeurEvacuateurFormula);

				Cell calculHChargeSeuilCell = exuRow.createCell(17);
				String calculHChargeSeuilFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHChargeSeuilCell, "")
						.setCellFormula(calculHChargeSeuilFormula);

				Cell calculDimResumeLCell = exuRow.createCell(19);
				String calculDimResumeLFormula = String.format("MROUND(%s%s+0.25,0.5)",
						CellReference.convertNumToColString(calculLargeurEvacuateurCell.getColumnIndex()),
						calculLargeurEvacuateurCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);

				Cell calculDimResumeHCell = exuRow.createCell(20);
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

		XlsUtils.mergeRow(computeContext, sheet, 0, 0, 20);

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
