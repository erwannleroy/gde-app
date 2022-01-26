package org.r1.gde.xls.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.r1.gde.XlsUtils.*;

@Component
@Slf4j
public class RetentionGenerator extends SheetGenerator {

	private int rowIndexRetention = 0;

	public static final String TITLE_SHEET = "Rétention des bassins";

	@Autowired
	private ParametresGenerator parametresGenerator;

	@Autowired
	ObjectifsRetentionGenerator dimensionnementGenerator;

	public void run() {

		log.info("Génération de l'onglet Rétention");

		rowIndexRetention = 0;

		try {
			generate();
		} catch (GDEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void generate() throws GDEException {

		// on commence au bord
		int indexColumn = 0;

		// une ligne vide
		rowIndexRetention++;
		rowIndexRetention++;

		int firstRow = rowIndexRetention;

		// titre
		Row title2ParamRow = sheet.createRow(rowIndexRetention);
		Cell title2ParamCell = title2ParamRow.createCell(indexColumn);
//		titleZone(computeContext, title2ParamCell, zone.nom);

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
		Cell capaRetCellCol2 = capaRetRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, capaRetCellCol2, "m3");

		rowIndexRetention++;

		Row blankRow2 = sheet.createRow(rowIndexRetention);
		blankRow2.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));
		Cell bl2Cell = blankRow2.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bl2Cell, "");
		Cell bl2CellCol2 = blankRow2.createCell(indexColumn);
		title3LeftTopBorder(computeContext, bl2CellCol2, "");

		rowIndexRetention++;

		Row capaCumulRow = sheet.createRow(rowIndexRetention);
		Cell capaCumulTitleCell = capaCumulRow.createCell(indexColumn);
		title3LeftBorder(computeContext, capaCumulTitleCell, "Capacité cumulée des bassins");
		Cell capaCumulTitleCellCol2 = capaCumulRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, capaCumulTitleCellCol2, "m3");

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

		Decanteur d = new Decanteur();
		d.setBv("bv 1");
		d.setHauteurDigue(5.0);
		d.setNom("nom 1");
		d.setProfondeur(12.0);
		d.setProfondeurDeversoir(20.0);
		d.setSurface(100.0);
		d.setZone("zone 1");

		Cell decNomCell = ouvrageRow.createCell(indexColumn);
		boldCell(computeContext, decNomCell, d.getNom());

		Cell decSurfCell = surfaceRow.createCell(indexColumn);
		standardCellDecimalNoComma(computeContext, decSurfCell, "").setCellValue(d.getSurface());

		Cell decProfCell = profRow.createCell(indexColumn);
		if (d.getProfondeur() != null) {
			standardCellDecimal1Comma(computeContext, decProfCell, "").setCellValue(d.getProfondeur());
		}

		Cell decProfDevCell = profDevRow.createCell(indexColumn);
		Double profondeurDeversoir = d.getProfondeurDeversoir();
		standardCell(computeContext, decProfDevCell, profondeurDeversoir != null ? profondeurDeversoir.toString() : "");
		if (profondeurDeversoir != null) {
			standardCell(computeContext, decProfDevCell, "").setCellValue(profondeurDeversoir);
		} else {
			standardCell(computeContext, decProfDevCell, "").setCellFormula(
					parametresGenerator.parametres.get(ParametresGenerator.PARAM_DEC_PROFONDEUR_DEVERSOIR_PARAM));
			;
		}

		Cell decHauteurDigueCell = hauteurDigueRow.createCell(indexColumn);
		if (d.getHauteurDigue() != null) {
			standardCell(computeContext, decHauteurDigueCell, "").setCellValue(d.getHauteurDigue());
		} else {
			standardCell(computeContext, decHauteurDigueCell, "").setCellFormula(
					parametresGenerator.parametres.get(ParametresGenerator.PARAM_DEC_HAUTEUR_DIGUE_PARAM));
			;
		}

		Cell decCapaRetCell = capaRetRow.createCell(indexColumn);
		String capaRetFormula = String.format("INT(%s%s*(%s%s-%s%s+%s%s))",
				CellReference.convertNumToColString(decSurfCell.getColumnIndex()), decSurfCell.getRowIndex() + 1,
				CellReference.convertNumToColString(decProfCell.getColumnIndex()), decProfCell.getRowIndex() + 1,
				CellReference.convertNumToColString(decProfDevCell.getColumnIndex()), decProfDevCell.getRowIndex() + 1,
				CellReference.convertNumToColString(decHauteurDigueCell.getColumnIndex()),
				decHauteurDigueCell.getRowIndex() + 1);
		standardCellDecimalNoComma(computeContext, decCapaRetCell, "").setCellFormula(capaRetFormula);

		indexColumn++;

	}


	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
}
