package org.r1.gde.xls.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.service.ComputingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.r1.gde.XlsUtils.*;

@Component
@Slf4j
public class ObjectifsRetentionGenerator extends SheetGenerator {

	private int rowIndexDimensionnement = 0;
//	private int TAILLE_LOT_BV = 30;
	private Map<String, String> volumesBV;
	private static final String TITLE_SHEET = "Objectifs rétention";
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	private ParametresGenerator parametresGenerator;

	public void doRun() {

		log.info("Génération de l'onglet Dimensionnement");

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

		rowIndexDimensionnement = 0;
		volumesBV = new HashMap<>();

		generateTitleBlock();

		nbOuvragesTraites = 0;
		nbOuvragesTotal = countOuvragesTotal(bassins());

		generateBassins(bassins());

		sheet.setColumnWidth(0, 16 * 256);
		sheet.setColumnWidth(1, 14 * 256);
		sheet.setColumnWidth(2, 14 * 256);
		sheet.setColumnWidth(3, 14 * 256);
		sheet.setColumnWidth(4, 8 * 256);
		sheet.setColumnWidth(5, 14 * 256);
		sheet.setColumnWidth(6, 2 * 256);
		sheet.setColumnWidth(7, 14 * 256);
		sheet.setColumnWidth(8, 16 * 256);
		sheet.setColumnWidth(9, 2 * 256);
		sheet.setColumnWidth(10, 14 * 256);

		XlsUtils.borderMergedSheet(sheet);

		sheet.createFreezePane(0, 4);
		sheet.setRepeatingRows(new CellRangeAddress(2, 3, 0, 0));
		notifyListeners(SheetGeneratorEvent.OBJECTIFS_SHEET_GENERATED, null);

	}

	private int countOuvragesTotal(List<BVDecanteur> bvds) {
		return bvds.size();
	}

	private void generateBassins(List<BVDecanteur> bassinsLot) {

		// enteteGroup
		Row enteteGroup = sheet.createRow(rowIndexDimensionnement);
		short htitle = 40;
		enteteGroup.setHeightInPoints(htitle);

		XlsUtils.mergeRow(computeContext, sheet, rowIndexDimensionnement, 0, 5);
		Cell title2ParamCell = enteteGroup.createCell(0);
		title2(computeContext, title2ParamCell, "Paramètres hydrauliques des bassins versants associés aux ouvrages");

		XlsUtils.mergeRow(computeContext, sheet, rowIndexDimensionnement, 7, 8);
		Cell title2MetCell = enteteGroup.createCell(7);
		title2(computeContext, title2MetCell, "Données météorologiques");

		Cell title2DimCell = enteteGroup.createCell(10);
		title2(computeContext, title2DimCell, "Dimensionnement");

		rowIndexDimensionnement++;

		// entete tableau

		Row enteteColonneRow = sheet.createRow(rowIndexDimensionnement);

		Cell bassinSuperficie = enteteColonneRow.createCell(0);
		title3(computeContext, bassinSuperficie, "Bassin");

		Cell titleSuperficie = enteteColonneRow.createCell(1);
		title3(computeContext, titleSuperficie, "Superficie du BV alimentant le bassin (ha)");

		Cell titleLongueur = enteteColonneRow.createCell(2);
		title3(computeContext, titleLongueur, "Longueur hydraulique du bassin versant (m)");

		Cell titleDenivele = enteteColonneRow.createCell(3);
		title3(computeContext, titleDenivele, "Dénivelé du bassin versant (m)");

		Cell titlePente = enteteColonneRow.createCell(4);
		title3(computeContext, titlePente, "Pente (%)");

		Cell titleRuissellement = enteteColonneRow.createCell(5);
		title3(computeContext, titleRuissellement, "Coefficient de ruissellement du BV");

		Cell titlePluie = enteteColonneRow.createCell(7);
		title3(computeContext, titlePluie, "Temps de retour et durée de la pluie de référence choisis");

		Cell titlePrecipitations = enteteColonneRow.createCell(8);
		title3(computeContext, titlePrecipitations,
				"Quantité max de précipitations i(t;T) pour une durée de pluie t (min) et pour une période de retour T (années)");

		Cell titleVolume = enteteColonneRow.createCell(10);
		title3(computeContext, titleVolume, "Volume d'eau V à retenir dans le décanteur (m3)");

		for (BVDecanteur bv : bassinsLot) {

			rowIndexDimensionnement++;

			Row bassinRow = sheet.createRow(rowIndexDimensionnement);

			Cell nomCell = bassinRow.createCell(0);
			nomCell.setCellValue(bv.nomOuvrage);
			redBold(computeContext, nomCell, bv.nomOuvrage);

			Cell superficieCell = bassinRow.createCell(1);
			if (bv.surface != null) {
				standardCellDecimal2Comma(computeContext, superficieCell, "").setCellValue(bv.surface / 10000);
			}

			Cell longueurCell = bassinRow.createCell(2);
			if (bv.longueur != null) {
				standardCellDecimalNoComma(computeContext, longueurCell, "").setCellValue(bv.longueur);
			}

			Cell deniveleCell = bassinRow.createCell(3);
			if (bv.denivele != null) {
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(bv.denivele);
			}

			Cell penteCell = bassinRow.createCell(4);
			standardCellDecimalNoComma(computeContext, penteCell, "");
			penteCell.setCellFormula(String.format("(%s%s/%s%s)*100",
					CellReference.convertNumToColString(deniveleCell.getColumnIndex()), deniveleCell.getRowIndex() + 1,
					CellReference.convertNumToColString(longueurCell.getColumnIndex()),
					longueurCell.getRowIndex() + 1));

			Cell ruissellementCell = bassinRow.createCell(5);
			standardCell(computeContext, ruissellementCell, "")
					.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));

			Cell pluieCell = bassinRow.createCell(7);
			standardCell(computeContext, pluieCell, ParametresGenerator.DIM_TPS_RETOUR_DUREE_PLUIE_DEFAULT);

			Cell precipitationsCell = bassinRow.createCell(8);
			standardCell(computeContext, precipitationsCell, "").setCellFormula(
					parametresGenerator.parametres.get(ParametresGenerator.METEO_QTE_MAX_PRECIPITATIONS_PARAM));

			Cell volEauCell = bassinRow.createCell(10);
			String volEauFormula = String.format("INT((%s%s/1000)*(%s%s*10000)*%s%s)",
					CellReference.convertNumToColString(precipitationsCell.getColumnIndex()),
					precipitationsCell.getRowIndex() + 1,
					CellReference.convertNumToColString(superficieCell.getColumnIndex()),
					superficieCell.getRowIndex() + 1,
					CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
					ruissellementCell.getRowIndex() + 1);

			volumesBV.put(bv.nomOuvrage, XlsUtils.getReference(volEauCell));

//			log.info("Formule du volume d'eau : " + volEauFormula);
			redBoldDecimalNoComma(computeContext, volEauCell, "").setCellFormula(volEauFormula);

			nbOuvragesTraites++;
			double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
			notifyListeners(SheetGeneratorEvent.OBJECTIFS_SHEET_PROGRESS, (int) progress);

		}

	}

	public String getReferenceVolumeEauBV(String nomBV) throws BVNotFoundException {

		String volBV = volumesBV.get(nomBV);
		if (volBV == null) {
			throw new BVNotFoundException(nomBV);
		}
		return volBV;
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexDimensionnement);
		short htitle = 20;
		titleRow.setHeightInPoints(htitle);
		// une colonne vide
		int indexColumn = 0;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, 10);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Objectif de rétention pour chaque sous-bassin versant de la mine ";
		Cell headerCell = titleRow.createCell(indexColumn);
		title1(computeContext, headerCell, title).setCellFormula("CONCATENATE(\"" + title + "\","
				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexDimensionnement++;
		rowIndexDimensionnement++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
	
	@Override
	protected void detailError() {
		computeContext.getComputingResult().setObjRetComputeProgress(0);
		computeContext.getComputingResult().setObjRetComputeOk(false);
	}
	
	@Override
	protected List<String> getListErrors(ComputingResult cr) {
		return cr.getObjRetWarns();
	}

}
