package org.r1.gde.xls.generator;

import static org.r1.gde.XlsUtils.standardCell;
import static org.r1.gde.XlsUtils.decimalCell;
import static org.r1.gde.XlsUtils.decimalCellEmpty;
import static org.r1.gde.XlsUtils.title2;
import static org.r1.gde.XlsUtils.title3;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
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

	public static final double CST_COEFF_RUISS_DEFAULT = 0.9;
	public static final String CST_COEFF_RUISS_PARAM = "EXU_COEFF_RUISS";
	public static final String CST_VIT_ECOUL_INF_5_PARAM = "EXU_VIT_ECOUL_INF_5";
	public static final int CST_VIT_ECOUL_INF_5_DEFAULT = 1;
	public static final String CST_VIT_ECOUL_5_15_PARAM = "EXU_VIT_ECOUL_5_15";
	public static final int CST_VIT_ECOUL_5_15_DEFAULT = 2;
	public static final String CST_VIT_ECOUL_SUP_15_PARAM = "EXU_VIT_ECOUL_SUP_15";
	public static final int CST_VIT_ECOUL_SUP_15_DEFAULT = 4;
	public static final String CST_N_PARAM = "EXU_N";
	public static final double CST_N_DEFAULT = 0.4;
	public static final String CST_G_PARAM = "EXU_G";
	public static final double CST_G_DEFAULT = 9.81;
	public static final String CST_COEF_STRICKLER_PARAM = "CASSIS_COEF_STRICKLER";
	public static final double CST_COEF_STRICKLER_DEFAULT = 20.0;
	public static final String CST_PENTE_PARAM = "CASSIS_PENTE";
	public static final Double CST_PENTE_DEFAULT = 0.02;
	
	
	public static final String PARAM_DEC_PROFONDEUR_DEVERSOIR_PARAM = "PROF_DEV";
	public static final Integer PARAM_DEC_PROFONDEUR_DEVERSOIR_DEFAULT = 0;
	public static final String PARAM_DEC_HAUTEUR_DIGUE_PARAM = "HAUT_DIG";
	public static final Integer PARAM_DEC_HAUTEUR_DIGUE_DEFAULT = 0;
	
	
	public static final String OUVRAGE_H_LAME_EAU_PARAM = "EXU_H_LAME_EAU";
	public static final double OUVRAGE_H_LAME_EAU_DEFAULT = 0.4;
	public static final String OUVRAGE_REVANCHE_PARAM = "EXU_REVANCHE";
	public static final double OUVRAGE_REVANCHE_DEFAULT = 0.1;
	

	public static final String METEO_COEFF_MONTANA_A_PARAM = "METEO_MONTANA_A";
	public static final double METEO_COEFF_MONTANA_A_DEFAULT = 351.1;
	public static final String METEO_COEFF_MONTANA_B_PARAM = "METEO_MONTANA_B";
	public static final double METEO_COEFF_MONTANA_B_DEFAULT = -0.248;
	public static final String METEO_QTE_MAX_PRECIPITATIONS_PARAM = "METEO_QTE_MAX_PRECIPITATIONS";
	public static final double METEO_QTE_MAX_PRECIPITATIONS_DEFAULT = 59.8;
	public static final String METEO_TPS_CONCENTRATION_PARAM = "METEO_TPS_CONCENTRATION";
	public static final double METEO_TPS_CONCENTRATION_DEFAULT = 6;
	

	int indexColumn = 0;

	public void run() {

		log.info("Génération de l'onglet Paramètres");

		sheet = workbook().getSheet(TITLE_SHEET);
		
		if (null != sheet) {
			workbook().removeSheetAt(0);
		} 

		sheet = workbook().createSheet(TITLE_SHEET);
		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);

		sheet.setColumnWidth(0, 15);

		rowIndexParametres = 0;
		parametres = new HashMap<>();

		generateParametresGlobaux();

		generateParametresMeteo();

		generateConstantesDimensionnementDéfaut();

		generateParametresDecanteurs();

		generateParametresOuvragesTransit();

		int column = 0;
		while (column < 4) {
			sheet.autoSizeColumn(column);
			column++;
		}
		

		sheet.setColumnWidth(1, 20 * 256);
	}

	private void generateParametresGlobaux() {
		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres globaux");

		rowIndexParametres++;

		createParamText(GLO_NOM_MINE_PARAM, "Nom de la mine", "");
	}

	private void createParamText(String keyParam, String titleParam, String defaultValue) {
		Row row = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = row.createCell(indexColumn);
		title3(computeContext, titleParamCell, titleParam);
		Cell paramValueCell = row.createCell(indexColumn + 1);
		parametres.put(keyParam, XlsUtils.getReference(paramValueCell));
		standardCell(computeContext, paramValueCell, defaultValue);
		rowIndexParametres++;
	}
	
	private void createParamDecimalEmpty(String keyParam, String titleParam) {
		Row row = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = row.createCell(indexColumn);
		title3(computeContext, titleParamCell, titleParam);
		Cell paramValueCell = row.createCell(indexColumn + 1);
		parametres.put(keyParam, XlsUtils.getReference(paramValueCell));
		decimalCellEmpty(computeContext, paramValueCell);
		rowIndexParametres++;
	}
	
	private void createParamDecimal(String keyParam, String titleParam, double defaultValue) {
		Row row = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = row.createCell(indexColumn);
		title3(computeContext, titleParamCell, titleParam);
		Cell paramValueCell = row.createCell(indexColumn + 1);
		parametres.put(keyParam, XlsUtils.getReference(paramValueCell));
		decimalCell(computeContext, paramValueCell, defaultValue);
		rowIndexParametres++;
	}

	private void generateParametresMeteo() {

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Données météorologiques");

		rowIndexParametres++;

		createParamDecimalEmpty(METEO_QTE_MAX_PRECIPITATIONS_PARAM, "Quantité max de précipitations");
		createParamDecimalEmpty(METEO_COEFF_MONTANA_A_PARAM, "Coefficient de Montana A");
		createParamDecimalEmpty(METEO_COEFF_MONTANA_B_PARAM, "Coefficient de Montana B");
		createParamDecimal(METEO_TPS_CONCENTRATION_PARAM, "Temps de concentration minimal retenu (min)", METEO_TPS_CONCENTRATION_DEFAULT);

	}

	private void generateParametresDecanteurs() {
		int indexColumn = 0;

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres par défaut des décanteurs");

		rowIndexParametres++;

		createParamDecimal(PARAM_DEC_PROFONDEUR_DEVERSOIR_PARAM, "Profondeur de déversoir",
				PARAM_DEC_PROFONDEUR_DEVERSOIR_DEFAULT);
		createParamDecimal(PARAM_DEC_HAUTEUR_DIGUE_PARAM, "Hauteur de digue", PARAM_DEC_HAUTEUR_DIGUE_DEFAULT);
		
	}
	
	private void generateParametresOuvragesTransit() {

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Paramètres par défaut des ouvrages de transit");

		rowIndexParametres++;

		createParamDecimal(OUVRAGE_H_LAME_EAU_PARAM, "Hauteur de lame d'eau", OUVRAGE_H_LAME_EAU_DEFAULT);
		createParamDecimal(OUVRAGE_REVANCHE_PARAM, "Revanche", OUVRAGE_REVANCHE_DEFAULT);
		
	}

	private void generateConstantesDimensionnementDéfaut() {

		rowIndexParametres++;

		Row titleParam = sheet.createRow(rowIndexParametres);
		Cell titleParamCell = titleParam.createCell(0);
		title2(computeContext, titleParamCell, "Constantes de dimensionnement par défaut");

		rowIndexParametres++;

		createParamDecimal(CST_COEFF_RUISS_PARAM, "Coefficient de ruissellement", CST_COEFF_RUISS_DEFAULT);
		createParamDecimal(CST_VIT_ECOUL_INF_5_PARAM, "Vitesse d'écoulement si pente <= 5%",
				CST_VIT_ECOUL_INF_5_DEFAULT);
		createParamDecimal(CST_VIT_ECOUL_5_15_PARAM, "Vitesse d'écoulement si 5% < pente <= 15%",
				CST_VIT_ECOUL_5_15_DEFAULT);
		createParamDecimal(CST_VIT_ECOUL_SUP_15_PARAM, "Vitesse d'écoulement si pente > 15%",
				CST_VIT_ECOUL_SUP_15_DEFAULT);
		createParamDecimal(CST_N_PARAM, "Constante rhéologique", CST_N_DEFAULT);
		createParamDecimal(CST_G_PARAM, "Gravité", CST_G_DEFAULT);
		createParamDecimal(CST_COEF_STRICKLER_PARAM, "Coef. rugosié de Strickler (Ks)", CST_COEF_STRICKLER_DEFAULT);
		createParamDecimal(CST_PENTE_PARAM, "Pente fossé-cassis (m/m)", CST_PENTE_DEFAULT);
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
