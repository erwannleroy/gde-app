package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.boldCell;
import static org.r1.gde.XlsUtils.redBoldBorderBottom;
import static org.r1.gde.XlsUtils.redBoldBorderTop;
import static org.r1.gde.XlsUtils.standardCell;
import static org.r1.gde.XlsUtils.standardCellTopBorder;
import static org.r1.gde.XlsUtils.title1;
import static org.r1.gde.XlsUtils.title2;
import static org.r1.gde.XlsUtils.title3;
import static org.r1.gde.XlsUtils.title3BottomBorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BassinVersant;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ParametresGenerator extends SheetGenerator {

	private int rowIndexParametres = 0;
	public Map<String, String> parametres;
	private static final String TITLE_SHEET = "Paramètres";
	public static final String DIM_DENIVELE_PARAM = "DENIVELE";
	public static final String DIM_LONGUEUR_PARAM = "LONGUEUR";
	public static final String DIM_SURFACE_PARAM = "SURFACE";
	public static final String GLO_NOM_MINE_PARAM = "NOM_MINE";
	public static final Integer DIM_DENIVELE_DEFAULT = -1;
	public static final String DIM_COEF_RUISS_PARAM = "DIM_COEFF_RUISS";
	public static final Double DIM_COEFF_RUISS_DEFAULT = 0.9;
	public static final Integer DIM_LONGUEUR_DEFAULT = -1;
	public static final Double DIM_SURFACE_DEFAULT = new Double(-1);
	public static final String RET_PROFONDEUR_DEVERSOIR_PARAM = "PROF_DEV";
	public static final Integer RET_PROFONDEUR_DEVERSOIR_DEFAULT = 0;
	public static final String RET_HAUTEUR_DIGUE_PARAM = "HAUT_DIG";
	public static final Integer RET_HAUTEUR_DIGUE_DEFAULT = 0;
	public static final String EXU_COEF_RUISS_PARAM = "EXU_COEFF_RUISS";
	public static final Double EXU_COEFF_RUISS_DEFAULT = 0.9;
	public static final String EXU_VIT_ECOUL_INF_5_PARAM = "EXU_VIT_ECOUL_INF_5";
	public static final Integer EXU_VIT_ECOUL_INF_5_DEFAULT = 1;
	public static final String EXU_VIT_ECOUL_5_15_PARAM = "EXU_VIT_ECOUL_5_15";
	public static final Integer EXU_VIT_ECOUL_5_15_DEFAULT = 2;
	public static final String EXU_VIT_ECOUL_SUP_15_PARAM = "EXU_VIT_ECOUL_SUP_15";
	public static final Integer EXU_VIT_ECOUL_SUP_15_DEFAULT = 4;
	public static final String EXU_N_PARAM = "EXU_N";
	public static final Double EXU_N_DEFAULT = 0.4;
	public static final String EXU_G_PARAM = "EXU_G";
	public static final Double EXU_G_DEFAULT = 9.81;
	public static final String EXU_H_LAME_EAU_PARAM = "EXU_H_LAME_EAU";
	public static final Double EXU_H_LAME_EAU_DEFAULT = 0.4;
	public static final String EXU_REVANCHE_PARAM = "EXU_REVANCHE";
	public static final Double EXU_REVANCHE_DEFAULT = 0.1;
	int indexColumn = 0;

	@Override
	public void startGeneration() {

		log.info("Génération de l'onglet Paramètres");

		sheet = workbook().createSheet(TITLE_SHEET);

		sheet.setColumnWidth(0, 15);

		rowIndexParametres = 0;
		parametres = new HashMap<>();

		indexColumn++;

		generateTitleBlock();

		generateParametresGlobaux();

		generateParametresDimensionnement();

		generateParametresRetention();

		generateParametresExutoirs();
		
		int column = 0;
		while (column < 4) {
			sheet.autoSizeColumn(column);
			column++;
		}
	}

	private void generateParametresGlobaux() {

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 2);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres globaux");

		createParam(GLO_NOM_MINE_PARAM, "Nom de la mine", "");

	}

	private void createParam(String keyParam, String titleParam, String defaultValue) {
		Row row = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = row.createCell(indexColumn);
		title3(computeContext, titleParamCell, titleParam);
		Cell paramValueCell = row.createCell(indexColumn + 1);
		parametres.put(keyParam, XlsUtils.getReference(paramValueCell));
		standardCell(computeContext, paramValueCell, defaultValue);
		rowIndexParametres++;
	}

	private void generateParametresDimensionnement() {
		int indexColumn = 0;

		indexColumn++;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 2);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres de dimensionnement");

		rowIndexParametres++;
		
		createParam(DIM_DENIVELE_PARAM, "Dénivelé", DIM_DENIVELE_DEFAULT.toString());
		createParam(DIM_LONGUEUR_PARAM, "Longueur", DIM_LONGUEUR_DEFAULT.toString());
		createParam(DIM_SURFACE_PARAM, "Surface", DIM_SURFACE_DEFAULT.toString());
		createParam(DIM_COEF_RUISS_PARAM, "Coefficient de ruissellement", DIM_COEFF_RUISS_DEFAULT.toString());
	}

	private void generateParametresRetention() {
		int indexColumn = 0;

		indexColumn++;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 2);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres de rétention");

		rowIndexParametres++;
		
		createParam(RET_PROFONDEUR_DEVERSOIR_PARAM, "Profondeur de déversoir",
				RET_PROFONDEUR_DEVERSOIR_DEFAULT.toString());
		createParam(RET_HAUTEUR_DIGUE_PARAM, "Hauteur de digue", RET_HAUTEUR_DIGUE_DEFAULT.toString());
	}

	private void generateParametresExutoirs() {
		int indexColumn = 0;

		indexColumn++;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 2);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres de rétention");

		rowIndexParametres++;
		
		createParam(EXU_COEF_RUISS_PARAM, "Coefficient de ruissellement", EXU_COEFF_RUISS_DEFAULT.toString());
		createParam(EXU_VIT_ECOUL_INF_5_PARAM, "Vitesse d'écoulement si pente <= 5%",
				EXU_VIT_ECOUL_INF_5_DEFAULT.toString());
		createParam(EXU_VIT_ECOUL_5_15_PARAM, "Vitesse d'écoulement si 5% < pente <= 15%",
				EXU_VIT_ECOUL_5_15_DEFAULT.toString());
		createParam(EXU_VIT_ECOUL_SUP_15_PARAM, "Vitesse d'écoulement si pente > 15%",
				EXU_VIT_ECOUL_SUP_15_DEFAULT.toString());
		createParam(EXU_G_PARAM, "Gravité", EXU_G_DEFAULT.toString());
		createParam(EXU_H_LAME_EAU_PARAM, "Hauteur de lame d'eau", EXU_H_LAME_EAU_DEFAULT.toString());
		createParam(EXU_REVANCHE_PARAM, "Revanche", EXU_REVANCHE_DEFAULT.toString());

	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexParametres);

		XlsUtils.mergeRow(computeContext, sheet, 0, 0, 10);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Paramètres globaux";
		Cell headerCell = titleRow.createCell(0);
		title1(computeContext, headerCell, title);

		rowIndexParametres++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
