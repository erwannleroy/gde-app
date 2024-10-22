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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.r1.gde.XlsUtils.*;

@Component
@Slf4j
public class TCGenerator extends SheetGenerator {

	private int rowIndexTC = 0;
	private static final String TITLE_SHEET = "Temps de concentration";
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;
	private Map<String, String> tcExu;

	@Autowired
	ParametresGenerator parametresGenerator;

	public void doRun() {
		log.info("Calcul des temps de concentration");

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

		rowIndexTC = 0;

		tcExu = new HashMap<>();

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
		sheet.setColumnWidth(7, 1 * 256);
		sheet.setColumnWidth(8, 12 * 256);
		sheet.setColumnWidth(9, 12 * 256);
		sheet.setColumnWidth(10, 1 * 256);
		sheet.setColumnWidth(11, 12 * 256);
		sheet.setColumnWidth(12, 12 * 256);
		sheet.setColumnWidth(13, 1 * 256);
		sheet.setColumnWidth(14, 10 * 256);
		sheet.setColumnWidth(15, 10 * 256);
		sheet.setColumnWidth(16, 10 * 256);
		sheet.setColumnWidth(17, 12 * 256);
		sheet.setColumnWidth(18, 12 * 256);
		sheet.setColumnWidth(19, 1 * 256);
		sheet.setColumnWidth(20, 16 * 256);
		sheet.setColumnWidth(21,1  * 256);
		sheet.setColumnWidth(22, 9 * 256);
		sheet.setColumnWidth(23, 9 * 256);

		XlsUtils.borderMergedSheet(sheet);
		
		sheet.setRepeatingRows(new CellRangeAddress(2, 3, 0, 0));
		sheet.createFreezePane(0, 4);

		notifyListeners(SheetGeneratorEvent.TC_SHEET_GENERATED, null);
	}

	private void generateEntete() {
		// une ligne vide
		rowIndexTC++;

		// enteteGroup
		Row enteteRow = sheet.createRow(rowIndexTC);
		short htitle = 35;
		enteteRow.setHeightInPoints(htitle);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexTC, 0, 6);
		Cell caracExuTitleCell = enteteRow.createCell(0);
		title2(computeContext, caracExuTitleCell, "Caractéristiques des bassins versants associés aux exutoires");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexTC, 8, 9);
		Cell davarTitleCell = enteteRow.createCell(8);
		title2TC(computeContext, davarTitleCell, "DAVAR", TypeFormuleTC.DAVAR, parametresGenerator);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexTC, 11, 12);
		Cell meunierMathysTitleCell = enteteRow.createCell(11);
		title2TC(computeContext, meunierMathysTitleCell, "Meunier-Mathys", TypeFormuleTC.MEUNIER_MATHYS, parametresGenerator);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexTC, 14, 18);
		Cell monnierTitleCell = enteteRow.createCell(14);
		title2TC(computeContext, monnierTitleCell, "Monnier", TypeFormuleTC.MONNIER, parametresGenerator);

		Cell giandottiTitleCell = enteteRow.createCell(20);
		title2TC(computeContext, giandottiTitleCell, "Giandotti", TypeFormuleTC.GIANDOTTI, parametresGenerator);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexTC, 22, 23);
		Cell syntheseTitleCell = enteteRow.createCell(22);
		title2(computeContext, syntheseTitleCell, "Synthèse");

		rowIndexTC++;

		// entete tableau
		Row columnRow = sheet.createRow(rowIndexTC);

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


		// une séparation
		idxEntete++;

		Cell vitEcoulCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, vitEcoulCellTitle, "Vitesse d'écoulement (m/s)");

		Cell calculTpsConcDAVARCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, calculTpsConcDAVARCellTitle, "Calcul du temps de concentration (mn)");

		// une séparation
		idxEntete++;

		Cell coefDegradationCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, coefDegradationCellTitle, "Coeffcient de dégradation (K)");

		Cell calculTpsConcMeunierMathysCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, calculTpsConcMeunierMathysCellTitle, "Calcul du temps de concentration (mn)");

		// une séparation
		idxEntete++;

		Cell perimetreCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, perimetreCellTitle, "Périmètre (km)");

		Cell kGCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, kGCellTitle, "Indice de compacité de Gravelius");

		Cell leQCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, leQCellTitle, "Longueur équivalente (m)");

		Cell penteMonnierCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, penteMonnierCellTitle, "Pente moyenne (%)");

		Cell calculTpsConcMonnierCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, calculTpsConcMonnierCellTitle, "Calcul du temps de concentration (mn)");

		// une séparation
		idxEntete++;

		Cell calculTpsConcGiandottiCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, calculTpsConcGiandottiCellTitle, "Calcul du temps de concentration (mn)");

		// une séparation
		idxEntete++;

		Cell tpsConcChoisiCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, tpsConcChoisiCellTitle, "Temps de concentration choisi (mn)");

		Cell tpsConcRetenuCellTitle = columnRow.createCell(idxEntete++);
		title3(computeContext, tpsConcRetenuCellTitle, "Temps de concentration retenu (mn)");

		rowIndexTC++;

	}

	private void generateCreeks(List<Creek> creeks) {

		for (Creek c : creeks) {
			nbOuvragesTotal += c.exutoires.size();
		}
		
		log.info("Génération d'un lot de creeks, génération des autres colonnes");
		for (Creek c : creeks) {

			Row creekRow = sheet.createRow(rowIndexTC);

			if (c.exutoires.size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 0, rowIndexTC,
						rowIndexTC + c.exutoires.size() - 1);
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

				// DAVAR
				Cell ecoulementCell = exuRow.createCell(8);
				String ecoulementFormula = String.format("IF(%s%s<5,\"1\", IF(%s%s>15, \"4\", \"2\"))",
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1);
				standardCell(computeContext, ecoulementCell, "").setCellFormula(ecoulementFormula);

				Cell calculTpsConcDAVARCell = exuRow.createCell(9);
				String calculTpsConcDAVARFormula = String.format("%s%s/%s%s/60",
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1,
						CellReference.convertNumToColString(ecoulementCell.getColumnIndex()),
						ecoulementCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcDAVARCell, "").setCellFormula(calculTpsConcDAVARFormula);

				// Meunier-Mathys
				Cell coefDegradCell = exuRow.createCell(11);
				standardCell(computeContext, coefDegradCell, "")
						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_DEGRADATION_K_PARAM));

				Cell calculTpsConcMMCell = exuRow.createCell(12);
				String calculTpsConcMMFormula = String.format("%s%s*((%s%s*0.01)^0.312)*((%s%s)^-0.625)",
						CellReference.convertNumToColString(coefDegradCell.getColumnIndex()),
						coefDegradCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()),
						penteCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcMMCell, "").setCellFormula(calculTpsConcMMFormula);

				// Monnier
				Cell exuPerimCell = exuRow.createCell(14);
				standardCellDecimal2Comma(computeContext, exuPerimCell, "").setCellValue(e.getPerimetre());

				Cell kGCell = exuRow.createCell(15);
				String kGFormula = String.format("(%s%s/1000)/(2*SQRT(PI()*%s%s*0.01))",
						CellReference.convertNumToColString(exuPerimCell.getColumnIndex()),
						exuPerimCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, kGCell, "").setCellFormula(kGFormula);

				Cell leqCell = exuRow.createCell(16);
				String leqFormula = String.format("(%s%s*SQRT(%s%s*10000)/1.12)*(1+SQRT(1-(1.12/%s%s)^2))",
						CellReference.convertNumToColString(kGCell.getColumnIndex()),
						kGCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1,
						CellReference.convertNumToColString(kGCell.getColumnIndex()),
						kGCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, leqCell, "").setCellFormula(leqFormula);

				Cell penteMonnierCell = exuRow.createCell(17);
				String penteMonnierFormula = String.format("(%s%s/%s%s*100)",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(leqCell.getColumnIndex()),
						leqCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, penteMonnierCell, "").setCellFormula(penteMonnierFormula);

				Cell calculTpsConcMonnierCell = exuRow.createCell(18);
				String calculTpsConcMonnierFormula = String.format("(3.28*(1.1-%s)*SQRT(%s%s))/POWER(%s%s,1/3)",
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM),
						CellReference.convertNumToColString(leqCell.getColumnIndex()),
						leqCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteMonnierCell.getColumnIndex()),
						penteMonnierCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcMonnierCell, "").setCellFormula(calculTpsConcMonnierFormula);


				// Giandotti
				Cell calculTpsConcGiandottiCell = exuRow.createCell(20);
				String calculTpsConcGiandottiFormula = String.format("(24*(%s%s^0.5) + 0.09*%s%s)/((%s%s/100)*%s%s)^0.5",
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()),
						penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcGiandottiCell, "").setCellFormula(calculTpsConcGiandottiFormula);


				// Synthèse
				Cell tpsConcChoisiCell = exuRow.createCell(22);
				String tpsConcChoisiFormula = String.format("IF(%s=\"DAVAR\", %s%s, IF(%s=\"MEUNIER_MATHYS\", %s%s, IF(%s=\"MONNIER\", %s%s, IF(%s=\"GIANDOTTI\", %s%s, \"Erreur\"))))",
						parametresGenerator.parametres.get(ParametresGenerator.FORMULE_TC_PARAM),
						CellReference.convertNumToColString(calculTpsConcDAVARCell.getColumnIndex()),
						calculTpsConcDAVARCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.FORMULE_TC_PARAM),
						CellReference.convertNumToColString(calculTpsConcMMCell.getColumnIndex()),
						calculTpsConcMMCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.FORMULE_TC_PARAM),
						CellReference.convertNumToColString(calculTpsConcMonnierCell.getColumnIndex()),
						calculTpsConcMonnierCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.FORMULE_TC_PARAM),
						CellReference.convertNumToColString(calculTpsConcGiandottiCell.getColumnIndex()),
						calculTpsConcGiandottiCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, tpsConcChoisiCell, "").setCellFormula(tpsConcChoisiFormula);

				Cell tpsConcRetenuCell = exuRow.createCell(23);
				String tpsConcRetenuFormula = String.format("IF(%s%s>%s,%s%s, %s)",
						CellReference.convertNumToColString(tpsConcChoisiCell.getColumnIndex()),
						tpsConcChoisiCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM),
						CellReference.convertNumToColString(tpsConcChoisiCell.getColumnIndex()),
						tpsConcChoisiCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM));
				standardCellDecimal2Comma(computeContext, tpsConcRetenuCell, "").setCellFormula(tpsConcRetenuFormula);

				tcExu.put(e.getNom(), XlsUtils.getReference(tpsConcRetenuCell));

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.TC_SHEET_PROGRESS, (int) progress);

				rowIndexTC++;

				exuRow = sheet.createRow(rowIndexTC);
			}

			short spaceBV = 5;
			exuRow.setHeightInPoints(spaceBV);

			rowIndexTC++;
		}

	}
	
	@Override
	protected List<String> getListErrors(ComputingResult cr) {
		return cr.getTcWarns();
	}


	public String getReferenceTC(String nomExu) throws ExutoireNotFoundException {

		String tcRetenuExu = tcExu.get(nomExu);
		if (tcRetenuExu == null) {
			throw new ExutoireNotFoundException(nomExu);
		}
		return tcRetenuExu;
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexTC);

		short htitle = 20;
		titleRow.setHeightInPoints(htitle);

		XlsUtils.mergeRow(computeContext, sheet, 0, 0, 23);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Temps de concentration selon différentes formules";
		Cell headerCell = titleRow.createCell(0);
		title1(computeContext, headerCell, title);

		rowIndexTC++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
	
	@Override
	protected void detailError() {
		computeContext.getComputingResult().setTcComputeProgress(0);
		computeContext.getComputingResult().setTcComputeOk(false);
	}
}
