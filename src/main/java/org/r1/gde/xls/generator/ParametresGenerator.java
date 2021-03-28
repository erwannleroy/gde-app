package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.standardCell;
import static org.r1.gde.XlsUtils.title2;
import static org.r1.gde.XlsUtils.title3;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.r1.gde.XlsUtils;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ParametresGenerator extends SheetGenerator {

	private int rowIndexParametres = 0;
	public Map<String, String> parametres;
	private static final String TITLE_SHEET = "Paramètres";
	public static final String DIM_TPS_RETOUR_DUREE_PLUIE_PARAM = "TPS_RETOUR_PLUIE";

	
	public static final String DIM_TPS_RETOUR_DUREE_PLUIE_DEFAULT = "2H/2ANS";

	public static final String GLO_NOM_MINE_PARAM = "NOM_MINE";
	
	public static final String CST_COEFF_RUISS_DEFAULT = "0,9";
	public static final String CST_COEFF_RUISS_PARAM = "EXU_COEFF_RUISS";
	public static final String CST_VIT_ECOUL_INF_5_PARAM = "EXU_VIT_ECOUL_INF_5";
	public static final Integer CST_VIT_ECOUL_INF_5_DEFAULT = 1;
	public static final String CST_VIT_ECOUL_5_15_PARAM = "EXU_VIT_ECOUL_5_15";
	public static final Integer CST_VIT_ECOUL_5_15_DEFAULT = 2;
	public static final String CST_VIT_ECOUL_SUP_15_PARAM = "EXU_VIT_ECOUL_SUP_15";
	public static final Integer CST_VIT_ECOUL_SUP_15_DEFAULT = 4;
	public static final String CST_N_PARAM = "EXU_N";
	public static final String CST_N_DEFAULT = "0,4";
	public static final String CST_G_PARAM = "EXU_G";
	public static final String CST_G_DEFAULT = "9,81";
	public static final String CST_COEF_STRICKLER_PARAM = "CASSIS_COEF_STRICKLER";
	public static final String CST_COEF_STRICKLER_DEFAULT = "20,0";
	
	
	public static final String OUVRAGE_PROFONDEUR_DEVERSOIR_PARAM = "PROF_DEV";
	public static final Integer OUVRAGE_PROFONDEUR_DEVERSOIR_DEFAULT = 0;
	public static final String OUVRAGE_HAUTEUR_DIGUE_PARAM = "HAUT_DIG";
	public static final Integer OUVRAGE_HAUTEUR_DIGUE_DEFAULT = 0;
	public static final String OUVRAGE_H_LAME_EAU_PARAM = "EXU_H_LAME_EAU";
	public static final String OUVRAGE_H_LAME_EAU_DEFAULT = "0,4";
	public static final String OUVRAGE_REVANCHE_PARAM = "EXU_REVANCHE";
	public static final String OUVRAGE_REVANCHE_DEFAULT = "0,1";
	public static final String OUVRAGE_PENTE_PARAM = "CASSIS_PENTE";
	public static final String OUVRAGE_PENTE_DEFAULT = "0,02";
	
	public static final String METEO_COEFF_MONTANA_A_PARAM = "METEO_MONTANA_A";
	public static final String METEO_COEFF_MONTANA_A_DEFAULT = "351,1";
	public static final String METEO_COEFF_MONTANA_B_PARAM = "METEO_MONTANA_B";
	public static final String METEO_COEFF_MONTANA_B_DEFAULT = "-0,248";
	public static final String METEO_QTE_MAX_PRECIPITATIONS_PARAM = "METEO_QTE_MAX_PRECIPITATIONS";
	public static final String METEO_QTE_MAX_PRECIPITATIONS_DEFAULT = "59,8";


	int indexColumn = 0;

	@Override
	public void startGeneration() {

		log.info("Génération de l'onglet Paramètres");

		sheet = workbook().createSheet(TITLE_SHEET);

		sheet.setColumnWidth(0, 15);

		rowIndexParametres = 0;
		parametres = new HashMap<>();

		generateParametresGlobaux();

		generateParametresMeteo();

		generateParametresConstantes();

		generateParametresOuvrages();

		int column = 0;
		while (column < 4) {
			sheet.autoSizeColumn(column);
			column++;
		}
	}

	private void generateParametresGlobaux() {
		int indexColumn = 0;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 1);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres globaux");

		rowIndexParametres++;

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

	private void generateParametresMeteo() {
		int indexColumn = 0;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 1);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Données météorologiques");

		rowIndexParametres++;

		createParam(METEO_QTE_MAX_PRECIPITATIONS_PARAM, "Quantité max de précipitations",
				METEO_QTE_MAX_PRECIPITATIONS_DEFAULT.toString());
		createParam(METEO_COEFF_MONTANA_A_PARAM, "Coefficient de Montana A", METEO_COEFF_MONTANA_A_DEFAULT.toString());
		createParam(METEO_COEFF_MONTANA_B_PARAM, "Coefficient de Montana B", METEO_COEFF_MONTANA_B_DEFAULT.toString());

	}

	private void generateParametresOuvrages() {
		int indexColumn = 0;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 1);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres des décanteurs");

		rowIndexParametres++;

		createParam(OUVRAGE_PROFONDEUR_DEVERSOIR_PARAM, "Profondeur de déversoir",
				OUVRAGE_PROFONDEUR_DEVERSOIR_DEFAULT.toString());
		createParam(OUVRAGE_HAUTEUR_DIGUE_PARAM, "Hauteur de digue", OUVRAGE_HAUTEUR_DIGUE_DEFAULT.toString());
		createParam(OUVRAGE_H_LAME_EAU_PARAM, "Hauteur de lame d'eau", OUVRAGE_H_LAME_EAU_DEFAULT.toString());
		createParam(OUVRAGE_REVANCHE_PARAM, "Revanche", OUVRAGE_REVANCHE_DEFAULT.toString());
		createParam(OUVRAGE_PENTE_PARAM, "Pente fossé-cassis (m/m)", OUVRAGE_PENTE_DEFAULT.toString());
		}

	private void generateParametresConstantes() {
		int indexColumn = 0;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		XlsUtils.mergeRowBothBorder(computeContext, sheet, rowIndexParametres, indexColumn, indexColumn + 1);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres des exutoires");

		rowIndexParametres++;

		createParam(CST_COEFF_RUISS_PARAM, "Coefficient de ruissellement", CST_COEFF_RUISS_DEFAULT);
		createParam(CST_VIT_ECOUL_INF_5_PARAM, "Vitesse d'écoulement si pente <= 5%",
				CST_VIT_ECOUL_INF_5_DEFAULT.toString());
		createParam(CST_VIT_ECOUL_5_15_PARAM, "Vitesse d'écoulement si 5% < pente <= 15%",
				CST_VIT_ECOUL_5_15_DEFAULT.toString());
		createParam(CST_VIT_ECOUL_SUP_15_PARAM, "Vitesse d'écoulement si pente > 15%",
				CST_VIT_ECOUL_SUP_15_DEFAULT.toString());
		createParam(CST_N_PARAM, "Constante rhéologique", CST_N_DEFAULT.toString());
		createParam(CST_G_PARAM, "Gravité", CST_G_DEFAULT.toString());
		createParam(CST_COEF_STRICKLER_PARAM, "Coef. rugosié de Strickler (Ks)",
				CST_COEF_STRICKLER_DEFAULT.toString());

	}


	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
