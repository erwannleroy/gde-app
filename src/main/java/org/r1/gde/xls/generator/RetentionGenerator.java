package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.boldCell;
import static org.r1.gde.XlsUtils.colorCell;
import static org.r1.gde.XlsUtils.objectifRetentionCell;
import static org.r1.gde.XlsUtils.redBoldVATop;
import static org.r1.gde.XlsUtils.standardCell;
import static org.r1.gde.XlsUtils.standardCellDecimal1Comma;
import static org.r1.gde.XlsUtils.standardCellDecimalNoComma;
import static org.r1.gde.XlsUtils.standardCellDecimalNoCommaVATop;
import static org.r1.gde.XlsUtils.standardCellNoBorder;
import static org.r1.gde.XlsUtils.standardCellVATop;
import static org.r1.gde.XlsUtils.title1;
import static org.r1.gde.XlsUtils.title2;
import static org.r1.gde.XlsUtils.title3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.service.ComputingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetentionGenerator extends SheetGenerator {

	private int rowIndexRetention = 0;
//	private int TAILLE_LOT_DEC = 30;
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	public static final String TITLE_SHEET = "Rétention des bassins";

	@Autowired
	private ParametresGenerator parametresGenerator;

	@Autowired
	ObjectifsRetentionGenerator dimensionnementGenerator;

	public void doRun() throws GDEException {

		log.info("Génération de l'onglet Rétention");

		sheet = workbook().getSheet(TITLE_SHEET);

		if (null != sheet) {
			workbook().removeSheetAt(workbook().getSheetIndex(sheet));
		}

		sheet = workbook().createSheet(TITLE_SHEET);

		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(PrintSetup.A4_PAPERSIZE);
		sheet.setMargin(Sheet.RightMargin, 0.4);
		sheet.setMargin(Sheet.LeftMargin, 0.4);
		sheet.setMargin(Sheet.TopMargin, 0.4);
		sheet.setMargin(Sheet.BottomMargin, 0.4);

		rowIndexRetention = 0;

		generateTitleBlock();

		generateLegend();

		nbOuvragesTraites = 0;
		nbOuvragesTotal = countOuvragesTotal(zones());

		generateEntete();

		if (zones() != null) {
			for (Zone z : zones()) {
				generateZone(z);
			}
		}

		sheet.setColumnWidth(0, 14 * 256);
		sheet.setColumnWidth(1, 14 * 256);
		sheet.setColumnWidth(2, 12 * 256);
		sheet.setColumnWidth(3, 1 * 256);
		sheet.setColumnWidth(4, 14 * 256);
		sheet.setColumnWidth(5, 12 * 256);
		sheet.setColumnWidth(6, 12 * 256);
		sheet.setColumnWidth(7, 12 * 256);
		sheet.setColumnWidth(8, 12 * 256);
		sheet.setColumnWidth(9, 10 * 256);
		sheet.setColumnWidth(10, 1 * 256);
		sheet.setColumnWidth(11, 10 * 256);
		sheet.setColumnWidth(12, 10 * 256);

		XlsUtils.borderMergedSheet(sheet);

		sheet.createFreezePane(0, 8);
		sheet.setRepeatingRows(new CellRangeAddress(6, 7, 0, 0));

		notifyListeners(SheetGeneratorEvent.RETENTION_SHEET_GENERATED, null);
	}

	private int countOuvragesTotal(List<Zone> zones) {
		int nb = 0;
		for (Zone z : zones) {
			nb += countZoneOuvrages(z.getBassins());
		}
		return nb;
	}

	private int countZoneOuvrages(List<BassinVersant> bvs) {
		int nb = 0;
		for (BassinVersant bv : bvs) {
			nb += bv.getOuvrages().size();
		}
		return nb;
	}

	private void generateEntete() {
		// une ligne vide
		rowIndexRetention++;
		rowIndexRetention++;

		// enteteGroup
		Row enteteRow = sheet.createRow(rowIndexRetention);
		short htitle = 35;
		enteteRow.setHeightInPoints(htitle);

		Cell titleZone = enteteRow.createCell(0);
		title2(computeContext, titleZone, "Zone");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 1, 2);
		Cell titleBV = enteteRow.createCell(1);
		title2(computeContext, titleBV, "Bassin versant");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 4, 9);
		Cell titleDimCell = enteteRow.createCell(4);
		title2(computeContext, titleDimCell, "Caractéristiques des bassins");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 11, 12);
		Cell titleCumulCell = enteteRow.createCell(11);
		title2(computeContext, titleCumulCell, "Capacité de rétention");

		rowIndexRetention++;

		// entete tableau
		Row columnRow = sheet.createRow(rowIndexRetention);

		Cell zoneCellTitle = columnRow.createCell(0);
		title3(computeContext, zoneCellTitle, "Nom");

		Cell bvCellTitle = columnRow.createCell(1);
		title3(computeContext, bvCellTitle, "Bassin versant");

		Cell objCapaCellTitle = columnRow.createCell(2);
		title3(computeContext, objCapaCellTitle, "Objectif de rétention du BV (m3)");

		Cell nomOuvrageCellTitle = columnRow.createCell(4);
		title3(computeContext, nomOuvrageCellTitle, "Nom de l'ouvrage");

		Cell surfaceCellTitle = columnRow.createCell(5);
		title3(computeContext, surfaceCellTitle, "Surface au sol (m²)");

		Cell profCellTitle = columnRow.createCell(6);
		title3(computeContext, profCellTitle, "Profondeur - déversoir inclus dans la profondeur (m)");

		Cell profDevCellTitle = columnRow.createCell(7);
		title3(computeContext, profDevCellTitle, "Profondeur de déversoir (m)");

		Cell digueCellTitle = columnRow.createCell(8);
		title3(computeContext, digueCellTitle, "Hauteur de digue (m)");

		Cell capaRetCellTitle = columnRow.createCell(9);
		title3(computeContext, capaRetCellTitle, "Capacité de rétention (m3)");

		Cell cumulCellTitle = columnRow.createCell(11);
		title3(computeContext, cumulCellTitle, "Capacité cumulée des bassins (m3)");

		Cell percentObjCellTitle = columnRow.createCell(12);
		title3(computeContext, percentObjCellTitle, "% de l'objectif - 2H/2ANS");

		rowIndexRetention++;

	}

	private void generateZone(Zone zone) throws GDEException {

		for (BassinVersant bv : zone.getBassins()) {

			Row bvRow = sheet.createRow(rowIndexRetention);

			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 0, rowIndexRetention,
						rowIndexRetention + bv.ouvrages.size() - 1);

				XlsUtils.mergeCol(computeContext, sheet, 1, rowIndexRetention,
						rowIndexRetention + bv.ouvrages.size() - 1);
			}
			Cell zoneCell = bvRow.createCell(0);
			redBoldVATop(computeContext, zoneCell, zone.nom);

			Cell bvCell = bvRow.createCell(1);
			redBoldVATop(computeContext, bvCell, bv.nom);

			Cell objRetCell = bvRow.createCell(2);
			log.info("objRetCell " + bv);
			standardCellDecimalNoCommaVATop(computeContext, objRetCell, "")
					.setCellFormula("INT(" + dimensionnementGenerator.getReferenceVolumeEauBV(bv.nom) + ")");
			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 2, rowIndexRetention,
						rowIndexRetention + bv.ouvrages.size() - 1);
			}

			Cell capaCumulCell = bvRow.createCell(11);
			log.info("capaCumulCell " + bv);
			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 11, rowIndexRetention,
						rowIndexRetention + bv.ouvrages.size() - 1);
			}

			Cell percentObjCell = bvRow.createCell(12);
			log.info("percentObjCell " + bv);
			String percentObjFormula = String.format("INT(%s%s*100/%s%s)",
					CellReference.convertNumToColString(capaCumulCell.getColumnIndex()),
					capaCumulCell.getRowIndex() + 1, CellReference.convertNumToColString(objRetCell.getColumnIndex()),
					objRetCell.getRowIndex() + 1);

			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeCol(computeContext, sheet, 12, rowIndexRetention,
						rowIndexRetention + bv.ouvrages.size() - 1);
			}

			List<Cell> cells = new ArrayList<>();

			Row decRow = bvRow;

			for (Decanteur d : bv.getOuvrages()) {

				Cell decNomCell = decRow.createCell(4);
				boldCell(computeContext, decNomCell, d.getNom());

				Cell decSurfCell = decRow.createCell(5);
				standardCellDecimalNoComma(computeContext, decSurfCell, "").setCellValue(d.getSurface());

				Cell decProfCell = decRow.createCell(6);
				if (d.getProfondeur() != null) {
					standardCellDecimal1Comma(computeContext, decProfCell, "").setCellValue(d.getProfondeur());
				}

				Cell decProfDevCell = decRow.createCell(7);
				Double profondeurDeversoir = d.getProfondeurDeversoir();
				standardCell(computeContext, decProfDevCell,
						profondeurDeversoir != null ? profondeurDeversoir.toString() : "");
				if (profondeurDeversoir != null) {
					standardCell(computeContext, decProfDevCell, "").setCellValue(profondeurDeversoir);
				} else {
					standardCell(computeContext, decProfDevCell, "").setCellFormula(parametresGenerator.parametres
							.get(ParametresGenerator.PARAM_DEC_PROFONDEUR_DEVERSOIR_PARAM));
				}

				Cell decHauteurDigueCell = decRow.createCell(8);
				if (d.getHauteurDigue() != null) {
					standardCell(computeContext, decHauteurDigueCell, "").setCellValue(d.getHauteurDigue());
				} else {
					standardCell(computeContext, decHauteurDigueCell, "").setCellFormula(
							parametresGenerator.parametres.get(ParametresGenerator.PARAM_DEC_HAUTEUR_DIGUE_PARAM));
				}

				Cell decCapaRetCell = decRow.createCell(9);
				String capaRetFormula = String.format("INT(%s%s*(%s%s-%s%s+%s%s))",
						CellReference.convertNumToColString(decSurfCell.getColumnIndex()),
						decSurfCell.getRowIndex() + 1,
						CellReference.convertNumToColString(decProfCell.getColumnIndex()),
						decProfCell.getRowIndex() + 1,
						CellReference.convertNumToColString(decProfDevCell.getColumnIndex()),
						decProfDevCell.getRowIndex() + 1,
						CellReference.convertNumToColString(decHauteurDigueCell.getColumnIndex()),
						decHauteurDigueCell.getRowIndex() + 1);
				standardCellDecimalNoComma(computeContext, decCapaRetCell, "").setCellFormula(capaRetFormula);

				cells.add(decCapaRetCell);

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.RETENTION_SHEET_PROGRESS, (int) progress);

				rowIndexRetention++;

				decRow = sheet.createRow(rowIndexRetention);

			}

			short spaceBV = 5;
			decRow.setHeightInPoints(spaceBV);

			String cumulFormula = "";
			for (Cell c : cells) {
				if (!StringUtils.isEmpty(cumulFormula)) {
					cumulFormula += "+";
				}
				cumulFormula += String.format("%s%s", CellReference.convertNumToColString(c.getColumnIndex()),
						c.getRowIndex() + 1);
			}

			standardCellVATop(computeContext, capaCumulCell, "").setCellFormula(cumulFormula);

			objectifRetentionCell(computeContext, percentObjCell, "").setCellFormula(percentObjFormula);

			FormulaEvaluator evaluator = workbook().getCreationHelper().createFormulaEvaluator();

			// existing Sheet, Row, and Cell setup
			evaluator.evaluateFormulaCell(percentObjCell);
			String msgPercentRet = "Adresse % retention : " + percentObjCell.getAddress().formatAsString()
					+ "\n Formule : " + percentObjCell.getCellFormula();
			log.info(msgPercentRet);
			try {
				double poc = percentObjCell.getNumericCellValue();
				log.info("Performance bv " + bv.nom + " : " + poc);
				this.computeContext.getPerformanceBVDecanteur().put(bv.nom, poc);
			} catch (Exception e) {
				super.processFormulaError(percentObjCell);
				break;
			}
		}
		rowIndexRetention++;

	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexRetention);

		short htitle = 20;
		titleRow.setHeightInPoints(htitle);
		// une colonne vide
		int indexColumn = 0;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, 12);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Capacité de rétention globale et comparaison à l'objectif 2H/2ANS";
		Cell headerCell = titleRow.createCell(indexColumn);
		title1(computeContext, headerCell, title);
		rowIndexRetention++;
	}

	private void generateLegend() {

		rowIndexRetention++;

		Row legend1Row = sheet.createRow(rowIndexRetention);
		Cell legend1Text = legend1Row.createCell(0);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 0, 3);
		Cell legend1Color = legend1Row.createCell(4);

		colorCell(computeContext, legend1Color, IndexedColors.BRIGHT_GREEN);
		standardCellNoBorder(computeContext, legend1Text, "capacité de rétention > 100%  2h/2ans");

		rowIndexRetention++;

		Row legend2Row = sheet.createRow(rowIndexRetention);
		Cell legend2Text = legend2Row.createCell(0);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 0, 3);
		Cell legend2Color = legend2Row.createCell(4);
		colorCell(computeContext, legend2Color, IndexedColors.YELLOW);
		standardCellNoBorder(computeContext, legend2Text, "80% < capacité de rétention < 100%  2h/2ans");

		rowIndexRetention++;

		Row legend3Row = sheet.createRow(rowIndexRetention);
		Cell legend3Text = legend3Row.createCell(0);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexRetention, 0, 3);
		Cell legend3Color = legend3Row.createCell(4);
		colorCell(computeContext, legend3Color, IndexedColors.LIGHT_ORANGE);
		standardCellNoBorder(computeContext, legend3Text, "capacité de rétention < 80%  2h/2ans");

	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
	
	@Override
	protected List<String> getListErrors(ComputingResult cr) {
		return cr.getRetBassinsWarns();
	}
	
	@Override
	protected void detailError() {
		computeContext.getComputingResult().setRetComputeProgress(0);
		computeContext.getComputingResult().setRetComputeOk(false);
	}
}
