package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RetentionGenerator extends SheetGenerator {

	private int rowIndexRetention = 0;
	private int TAILLE_LOT_DEC = 30;
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	public static final String TITLE_SHEET = "Rétention des bassins";

	@Autowired
	private ParametresGenerator parametresGenerator;

	@Autowired
	ObjectifsRetentionGenerator dimensionnementGenerator;

	public void run() {

		log.info("Génération de l'onglet Rétention");

		this.computeContext.getComputingResult().setRetentionComputing(true);

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
		sheet.setColumnWidth(0, 1);

		rowIndexRetention = 0;

		generateTitleBlock();

		generateLegend();

		nbOuvragesTraites = 0;
		nbOuvragesTotal = countOuvragesTotal(zones());

		if (zones() != null) {
			for (Zone z : zones()) {
				try {
					generateZone(z);
				} catch (GDEException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		int column = 0;
		while (column < TAILLE_LOT_DEC + 2) {
			sheet.autoSizeColumn(column);
			column++;
			if (column >= 2) {
				sheet.setColumnWidth(column, 5);
			}
		}
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

	private void generateZone(Zone zone) throws GDEException {

		List<BassinVersant> remainList = zone.getBassins();
		List<BassinVersant> bassinsSubList = new ArrayList<BassinVersant>();

		int nbDecanteurs = 0;

		for (BassinVersant bassin : remainList) {
			bassinsSubList.add(bassin);
			nbDecanteurs += bassin.getOuvrages().size();

			if (nbDecanteurs >= TAILLE_LOT_DEC || zone.getBassins().indexOf(bassin) == (remainList.size() - 1)) {
				generateLotZone(zone, bassinsSubList);
				sheet.setRowBreak(rowIndexRetention);
				bassinsSubList.clear();
				nbDecanteurs = 0;
			}

		}

	}

	private void generateLotZone(Zone zone, List<BassinVersant> bassins) throws GDEException {

		int columnSize = bassins.size() + 1;

		// on commence au bord
		int indexColumn = 0;

		// une ligne vide
		rowIndexRetention++;
		rowIndexRetention++;

		int firstRow = rowIndexRetention;

		// titre
		Row title2ParamRow = sheet.createRow(rowIndexRetention);
//		XlsUtils.mergeRowLeftBorder(computeContext, sheet, rowIndexRetention, indexColumn, columnSize);
		Cell title2ParamCell = title2ParamRow.createCell(indexColumn);
		titleZone(computeContext, title2ParamCell, zone.nom);

		rowIndexRetention++;

		// entete tableau
		Row bvRow = sheet.createRow(rowIndexRetention);
		Cell bvTitleCell = bvRow.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bvTitleCell, "");
		Cell bvTitleCellCol2 = bvRow.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bvTitleCellCol2, "");

		rowIndexRetention++;

		Row objRetRow = sheet.createRow(rowIndexRetention);
		Cell objRet = objRetRow.createCell(indexColumn);
		title3LeftBorder(computeContext, objRet, "Objectif de capacité de rétention du BV (m3) :");
		Cell objRetCol2 = objRetRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, objRetCol2, "V (m3) =");

		rowIndexRetention++;

		Row blankRow1 = sheet.createRow(rowIndexRetention);
		blankRow1.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));
		Cell bl1Cell = blankRow1.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bl1Cell, "");
		Cell bl1CellCol2 = bvRow.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bl1CellCol2, "");

		rowIndexRetention++;

		Row caracRow = sheet.createRow(rowIndexRetention);
		Cell caracCell = caracRow.createCell(indexColumn);
		title2LeftBorder(computeContext, caracCell, "Caractéristiques des bassins");

		rowIndexRetention++;

		Row ouvrageRow = sheet.createRow(rowIndexRetention);
		Cell ouvrageCell = ouvrageRow.createCell(indexColumn);
		title3LeftBorder(computeContext, ouvrageCell, "Nom de l'ouvrage");
		Cell ouvrageCellCol2 = ouvrageRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, ouvrageCellCol2, "");

		rowIndexRetention++;

		Row surfaceRow = sheet.createRow(rowIndexRetention);
		Cell surfaceCell = surfaceRow.createCell(indexColumn);
		title3LeftBorder(computeContext, surfaceCell, "Surface au sol");
		Cell surfaceCellCol2 = surfaceRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, surfaceCellCol2, "m²");

		rowIndexRetention++;

		Row profRow = sheet.createRow(rowIndexRetention);
		Cell profCell = profRow.createCell(indexColumn);
		title3LeftBorder(computeContext, profCell, "Profondeur (déversoir inclus dans la profondeur) ");
		Cell profCellCol2 = profRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, profCellCol2, "m");

		rowIndexRetention++;

		Row profDevRow = sheet.createRow(rowIndexRetention);
		Cell profDevCell = profDevRow.createCell(indexColumn);
		title3LeftBorder(computeContext, profDevCell, "Profondeur de déversoir ");
		Cell profDevCellCol2 = profDevRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, profDevCellCol2, "m");

		rowIndexRetention++;

		Row hauteurDigueRow = sheet.createRow(rowIndexRetention);
		Cell hauteurDigueCell = hauteurDigueRow.createCell(indexColumn);
		title3LeftBorder(computeContext, hauteurDigueCell, "Hauteur de digue");
		Cell hauteurDigueCellCol2 = hauteurDigueRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, hauteurDigueCellCol2, "m");

		rowIndexRetention++;

		Row capaRetRow = sheet.createRow(rowIndexRetention);
		Cell capaRetCell = capaRetRow.createCell(indexColumn);
		title3LeftBorder(computeContext, capaRetCell, "Capacité de rétention");
		makeBottomBorder(capaRetCell);
		Cell capaRetCellCol2 = capaRetRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, capaRetCellCol2, "m3");
		makeRightBottomBorder(capaRetCellCol2);
		rowIndexRetention++;

		Row blankRow2 = sheet.createRow(rowIndexRetention);
		blankRow2.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));
		Cell bCell = blankRow2.createCell(indexColumn);
		Cell bl2Cell = bCell;
		makeTopBottomBorder(bl2Cell);
		Cell bl2CellCol2 = bCell;
		makeTopBottomBorder(bl2CellCol2);

		rowIndexRetention++;

		Row capaCumulRow = sheet.createRow(rowIndexRetention);
		Cell capaCumulTitleCell = capaCumulRow.createCell(indexColumn);
		title3LeftBorder(computeContext, capaCumulTitleCell, "Capacité cumulée des bassins");
		makeLeftTopBorder(capaCumulTitleCell);
		Cell capaCumulTitleCellCol2 = capaCumulRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, capaCumulTitleCellCol2, "m3");
		makeRightTopBorder(capaCumulTitleCellCol2);

		rowIndexRetention++;

		Row percentObjRow = sheet.createRow(rowIndexRetention);
		Cell percentObjTitleCell = percentObjRow.createCell(indexColumn);
		title3LeftBottomBorder(computeContext, percentObjTitleCell, "% de l'objectif - 2H/2ANS");

		Cell percentObjTitleCellCol2 = percentObjRow.createCell(indexColumn + 1);
		title3RightBottomBorder(computeContext, percentObjTitleCellCol2, "%");

		rowIndexRetention++;

		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		int nbOuvrage = 0;

//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 4, rowIndexRetention - 4, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 5, rowIndexRetention - 4, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 4, rowIndexRetention - 1, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexRetention - 2, rowIndexRetention - 1, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexRetention - 3, rowIndexRetention - 1, 1, nbOuvrage + 1);

		for (BassinVersant bv : bassins) {

			Cell bvCell = bvRow.createCell(indexColumn);
			redBoldBorderTopLeftRight(computeContext, bvCell, bv.nom);
			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, bvRow.getRowNum(), indexColumn,
						indexColumn + bv.ouvrages.size() - 1);
			}
			makeAllBoldExceptBottomBorder(sheet, bvRow.getRowNum(), bvRow.getRowNum(), indexColumn,
					indexColumn + bv.ouvrages.size() - 1);

			Cell objRetCell = objRetRow.createCell(indexColumn);
			log.info("objRetCell " + bv);
			standardCellLeftRightBorderDecimalNoComma(computeContext, objRetCell, "")
					.setCellFormula("INT(" + dimensionnementGenerator.getReferenceVolumeEauBV(bv.nom) + ")");
			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, objRetRow.getRowNum(), indexColumn,
						indexColumn + bv.ouvrages.size() - 1);
			}
			makeAllBoldExceptTopBorder(sheet, objRetRow.getRowNum(), objRetRow.getRowNum(), indexColumn,
					indexColumn + bv.ouvrages.size() - 1);

			Cell capaCumulCell = capaCumulRow.createCell(indexColumn);

			Cell percentObjCell = percentObjRow.createCell(indexColumn);

			List<Cell> cells = new ArrayList<>();
			int currentD = 0;
			int nbD = bv.getOuvrages().size();

			for (Decanteur d : bv.getOuvrages()) {
				nbOuvrage++;
				currentD++;

				Cell decNomCell = ouvrageRow.createCell(indexColumn);
				boldCell(computeContext, decNomCell, d.getNom());
				if (nbD == 1) {
					makeAllBoldExceptBottomBorder(decNomCell);
				} else {
					if (currentD == 1) {
						makeLeftTopBorder(decNomCell);
					} else if (currentD == nbD) {
						makeRightTopBorder(decNomCell);
					} else {
						makeTopBorder(decNomCell);
					}
				}

				Cell decSurfCell = surfaceRow.createCell(indexColumn);
				standardCellDecimalNoComma(computeContext, decSurfCell, "").setCellValue(d.getSurface());
				if (nbD == 1) {
					makeLeftRightBorder(decSurfCell);
				} else {
					if (currentD == 1) {
						makeLeftBorder(decSurfCell);
					} else if (currentD == nbD) {
						makeRightBorder(decSurfCell);
					}
				}

				Cell decProfCell = profRow.createCell(indexColumn);
				if (d.getProfondeur() != null) {
					standardCellDecimal1Comma(computeContext, decProfCell, "").setCellValue(d.getProfondeur());
				}
				if (nbD == 1) {
					makeLeftRightBorder(decProfCell);
				} else {
					if (currentD == 1) {
						makeLeftBorder(decProfCell);
					} else if (currentD == nbD) {
						makeRightBorder(decProfCell);
					}
				}

				Cell decProfDevCell = profDevRow.createCell(indexColumn);
				Double profondeurDeversoir = d.getProfondeurDeversoir();
				standardCell(computeContext, decProfDevCell,
						profondeurDeversoir != null ? profondeurDeversoir.toString() : "");
				if (profondeurDeversoir != null) {
					standardCell(computeContext, decProfDevCell, "").setCellValue(profondeurDeversoir);
				} else {
					standardCell(computeContext, decProfDevCell, "").setCellFormula(parametresGenerator.parametres
							.get(ParametresGenerator.PARAM_DEC_PROFONDEUR_DEVERSOIR_PARAM));
					;
				}
				if (nbD == 1) {
					makeLeftRightBorder(decProfDevCell);
				} else {
					if (currentD == 1) {
						makeLeftBorder(decProfDevCell);
					} else if (currentD == nbD) {
						makeRightBorder(decProfDevCell);
					}
				}

				Cell decHauteurDigueCell = hauteurDigueRow.createCell(indexColumn);
				if (d.getHauteurDigue() != null) {
					standardCell(computeContext, decHauteurDigueCell, "").setCellValue(d.getHauteurDigue());
				} else {
					standardCell(computeContext, decHauteurDigueCell, "").setCellFormula(
							parametresGenerator.parametres.get(ParametresGenerator.PARAM_DEC_HAUTEUR_DIGUE_PARAM));
					;
				}
				if (nbD == 1) {
					makeLeftRightBorder(decHauteurDigueCell);
				} else {
					if (currentD == 1) {
						makeLeftBorder(decHauteurDigueCell);
					} else if (currentD == nbD) {
						makeRightBorder(decHauteurDigueCell);
					}
				}

				Cell decCapaRetCell = capaRetRow.createCell(indexColumn);
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
				if (nbD == 1) {
					makeLeftRightBottomBorder(decCapaRetCell);
				} else {
					if (currentD == 1) {
						makeLeftBottomBorder(decCapaRetCell);
					} else if (currentD == nbD) {
						makeRightBottomBorder(decCapaRetCell);
					} else {
						makeBottomBorder(decCapaRetCell);
					}
				}

				Cell bCell3 = blankRow2.createCell(indexColumn);
				makeTopBottomBorder(bCell3);
				indexColumn++;

				cells.add(decCapaRetCell);

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.RETENTION_SHEET_PROGRESS, (int) progress);

			}

			String cumulFormula = "";
			for (Cell c : cells) {
				if (!StringUtils.isEmpty(cumulFormula)) {
					cumulFormula += "+";
				}
				cumulFormula += String.format("%s%s", CellReference.convertNumToColString(c.getColumnIndex()),
						c.getRowIndex() + 1);
			}
			standardCellLeftRightBorder(computeContext, capaCumulCell, "").setCellFormula(cumulFormula);
			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, capaCumulRow.getRowNum(), capaCumulCell.getColumnIndex(),
						capaCumulCell.getColumnIndex() + bv.ouvrages.size() - 1);
			}
			makeAllBoldExceptBottomBorder(sheet, capaCumulRow.getRowNum(), capaCumulRow.getRowNum(),
					capaCumulCell.getColumnIndex(), capaCumulCell.getColumnIndex() + bv.ouvrages.size() - 1);

			if (bv.getOuvrages().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, percentObjRow.getRowNum(), percentObjCell.getColumnIndex(),
						percentObjCell.getColumnIndex() + bv.ouvrages.size() - 1);
			}
			String percentObjFormula = String.format("INT(%s%s*100/%s%s)",
					CellReference.convertNumToColString(capaCumulCell.getColumnIndex()),
					capaCumulCell.getRowIndex() + 1, CellReference.convertNumToColString(objRetCell.getColumnIndex()),
					objRetCell.getRowIndex() + 1);
			objectifRetentionCell(computeContext, percentObjCell, "").setCellFormula(percentObjFormula);
			makeAllBoldExceptTopBorder(sheet, percentObjRow.getRowNum(), percentObjRow.getRowNum(),
					percentObjCell.getColumnIndex(), percentObjCell.getColumnIndex() + bv.ouvrages.size() - 1);

		}

//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 4, rowIndexRetention - 4, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 5, rowIndexRetention - 4, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 4, rowIndexRetention - 1, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexRetention - 2, rowIndexRetention - 1, 1, nbOuvrage + 1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexRetention - 3, rowIndexRetention - 1, 1, nbOuvrage + 1);
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexRetention);

		// une colonne vide
		int indexColumn = 0;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT_DEC + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Capacité de rétention globale actuelle et comparaison à l'objectif 2H/2ANS";
		Cell headerCell = titleRow.createCell(indexColumn);
		title1(computeContext, headerCell, title);
		rowIndexRetention++;
	}

	private void generateLegend() {

		// une colonne vide
		int indexColumn = 0;

		rowIndexRetention++;
		rowIndexRetention++;

		Row legend1Row = sheet.createRow(rowIndexRetention);
		Cell legend1Text = legend1Row.createCell(indexColumn);
		Cell legend1Color = legend1Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend1Color, IndexedColors.BRIGHT_GREEN);
		standardCell(computeContext, legend1Text, "capacité de rétention > 100%  2h/2ans");

		rowIndexRetention++;

		Row legend2Row = sheet.createRow(rowIndexRetention);
		Cell legend2Text = legend2Row.createCell(indexColumn);
		Cell legend2Color = legend2Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend2Color, IndexedColors.YELLOW);
		standardCell(computeContext, legend2Text, "80% < capacité de rétention < 100%  2h/2ans");

		rowIndexRetention++;

		Row legend3Row = sheet.createRow(rowIndexRetention);
		Cell legend3Text = legend3Row.createCell(indexColumn);
		Cell legend3Color = legend3Row.createCell(indexColumn + 1);
		colorCell(computeContext, legend3Color, IndexedColors.LIGHT_ORANGE);
		standardCell(computeContext, legend3Text, "capacité de rétention < 80%  2h/2ans");

		rowIndexRetention++;

	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
}
