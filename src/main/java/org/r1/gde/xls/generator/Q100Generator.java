package org.r1.gde.xls.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.r1.gde.XlsUtils;
import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.Creek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.r1.gde.XlsUtils.*;

@Component
@Slf4j
public class Q100Generator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Q100";
	private static final int TAILLE_LOT = 30;
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	ParametresGenerator parametresGenerator;

	public void run() {
		log.info("Génération de l'onglet Exutoire");

		this.computeContext.getComputingResult().setQ100Computing(true);

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

		rowIndexExutoire = 0;

		generateTitleBlock();

		if (creeks() != null) {
			generateCreeks();
		}

		int column = 0;
		while (column < TAILLE_LOT + 2) {
			sheet.autoSizeColumn(column);
			column++;
			if (column >= 2) {
				sheet.setColumnWidth(column, 5);
			}
		}
		notifyListeners(SheetGeneratorEvent.Q100_SHEET_GENERATED, null);
	}

	private void generateCreeks() {

		List<Creek> remainList = Lists.newArrayList(creeks().iterator());
		List<List<Creek>> sharedCreeks = new ArrayList<List<Creek>>();

		log.info("Partage des creeks - debut");
		sharedCreeks = shareCreeks(sharedCreeks, remainList, new ArrayList<Creek>());
		log.info("Partage des creeks - fin");

		nbOuvragesTraites = 0;
		nbOuvragesTotal = countBVTotal(sharedCreeks);

		log.info("nombre de pages après répartition : " + sharedCreeks.size());
		for (List<Creek> creeks : sharedCreeks) {
			log.info("Génération d'un lot de creeks - debut");
			generateLotCreek(creeks);
			log.info("Génération d'un lot de creeks - fin");
			rowIndexExutoire++;
			sheet.setRowBreak(rowIndexExutoire);
			log.info("nouvelle page");
		}
	}

	private List<List<Creek>> shareCreeks(List<List<Creek>> sharedCreeks, List<Creek> remainCreeks, List<Creek> lot) {
		if (remainCreeks.size() > 0) {
			Creek next = remainCreeks.get(0);

			int nbBV = next.getExutoires().size();

			int nbBVLot = countBVLot(lot);
			if (nbBVLot == TAILLE_LOT) {
				sharedCreeks.add(lot);
				return shareCreeks(sharedCreeks, remainCreeks, new ArrayList<Creek>());
			}
			if (nbBVLot + nbBV > TAILLE_LOT) {
				List<BVExutoire> exus = next.getExutoires().subList(0, TAILLE_LOT - nbBVLot);
				Creek c2 = next.clone();
				c2.setExutoires(exus);
				lot.add(c2);
				sharedCreeks.add(lot);

				List<BVExutoire> exus3 = next.getExutoires().subList(TAILLE_LOT - nbBVLot, nbBV);
				Creek c3 = next.clone();
				c3.setExutoires(exus3);
				remainCreeks.remove(0);
				remainCreeks.add(0, c3);

				return shareCreeks(sharedCreeks, remainCreeks, new ArrayList<Creek>());
			} else {
				lot.add(next);
				// sharedCreeks.add(lot);
				remainCreeks.remove(0);
				return shareCreeks(sharedCreeks, remainCreeks, lot);
			}
		} else {
			if (lot.size() > 0) {
				sharedCreeks.add(lot);
			}
			return sharedCreeks;
		}
	}

	private int countBVTotal(List<List<Creek>> sharedCreeks) {
		int nb = 0;
		for (List<Creek> lot : sharedCreeks) {
			nb += countBVLot(lot);
		}
		return nb;
	}

	private int countBVLot(List<Creek> lot) {
		int nb = 0;
		for (Creek c : lot) {
			nb += c.getExutoires().size();
		}
		return nb;
	}

	private void generateLotCreek(List<Creek> creeks) {
		int nbOuvrage = countBVLot(creeks);

		// on commence au bord
		int indexColumn = 0;

		int firstRow = rowIndexExutoire;

		rowIndexExutoire++;

		log.info("Génération d'un lot de creeks, génération des 2ères colonnes");
		// entete tableau
		Row creekRow = sheet.createRow(rowIndexExutoire);
		Cell creekTitleCell = creekRow.createCell(indexColumn);
		title2(computeContext, creekTitleCell, "Creek récepteur");
		makeTopRightBottomBorder(creekTitleCell);

		rowIndexExutoire++;

		Row exutoireRow = sheet.createRow(rowIndexExutoire);
		Cell exutoireCell = exutoireRow.createCell(indexColumn);
		title3(computeContext, exutoireCell, "Exutoire");

		rowIndexExutoire++;

		Row superficieRow = sheet.createRow(rowIndexExutoire);
		Cell titleSuperficie = superficieRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleSuperficie, "Superficie BV (ha) :");
		rowIndexExutoire++;

		Row longueurRow = sheet.createRow(rowIndexExutoire);
		Cell titleLongueur = longueurRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleLongueur, "Longueur hydraulique BV (m)");
		rowIndexExutoire++;

		Row deniveleRow = sheet.createRow(rowIndexExutoire);
		Cell titleDenivele = deniveleRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleDenivele, "Dénivelé BV (m)");
		rowIndexExutoire++;

		Row penteRow = sheet.createRow(rowIndexExutoire);
		Cell titlePente = penteRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titlePente, "Pente BV (%)");
		rowIndexExutoire++;

		Row ruissellementRow = sheet.createRow(rowIndexExutoire);
		Cell titleRuissellement = ruissellementRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleRuissellement, "Coefficient de ruissellement");

		rowIndexExutoire++;

		Row ecoulementRow = sheet.createRow(rowIndexExutoire);
		Cell titleEcoulement = ecoulementRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleEcoulement, "Vitesse d'écoulement (m/s)");

		rowIndexExutoire++;

		Row tempsRetourRow = sheet.createRow(rowIndexExutoire);
		Cell titleTempsRetour = tempsRetourRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleTempsRetour, "Temps de retour choisi :");
		Cell titleTempsRetourCol2 = tempsRetourRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleTempsRetourCol2, "100 ans");

		rowIndexExutoire++;

		Row calculTpsConcentrationRow = sheet.createRow(rowIndexExutoire);
		Cell titleCalculTpsConcentration = calculTpsConcentrationRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleCalculTpsConcentration, "Calcul du temps de concentration (en min)");
		Cell titleCalculTpsConcentrationCol2 = calculTpsConcentrationRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculTpsConcentrationCol2, "Tc=");

		rowIndexExutoire++;

		Row tpsConcentrationRetenuRow = sheet.createRow(rowIndexExutoire);
		Cell titleTpsConcentrationRetenu = tpsConcentrationRetenuRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleTpsConcentrationRetenu, "Temps de concentration retenu (en min)");
		Cell titleCalculTpsConcentrationRetenuCol2 = tpsConcentrationRetenuRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculTpsConcentrationRetenuCol2, "Tc=");

		rowIndexExutoire++;

		Row intensiteAverseRow = sheet.createRow(rowIndexExutoire);
		Cell titleIntensiteAverse = intensiteAverseRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleIntensiteAverse, "Calcul de l'intensité de l'averse (mm/h)");
		Cell titleIntensiteAverseCol2 = intensiteAverseRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleIntensiteAverseCol2, "I(d,T)=");

		rowIndexExutoire++;

		Row calculDebitRow = sheet.createRow(rowIndexExutoire);
		Cell titleCalculDebit = calculDebitRow.createCell(indexColumn);
		title3LeftBorder(computeContext, titleCalculDebit, "Calcul du débit par la méthode rationnelle (m3/s)");
		makeBottomBorder(titleCalculDebit);
		Cell titleCalculDebitCol2 = calculDebitRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculDebitCol2, "Q pointe =");
		makeRightBottomBorder(titleCalculDebitCol2);

		rowIndexExutoire++;

		// une ligne vide
		Row blankRow = sheet.createRow(rowIndexExutoire);
		blankRow.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));

		rowIndexExutoire++;

		Row title2Row = sheet.createRow(rowIndexExutoire);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, nbOuvrage + 1);
		title2Row.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Dimensionnement des sections des ouvrages de transit pour une crue centennale";
		Cell headerCell = title2Row.createCell(indexColumn);
		XlsUtils.title2(computeContext, headerCell, title);
		makeBoldBorder(sheet, rowIndexExutoire, rowIndexExutoire, indexColumn, nbOuvrage + 1);
		makeBoldBorder(headerCell);

		rowIndexExutoire++;

		Row lameEauRow = sheet.createRow(rowIndexExutoire);
		Cell lameEauCell = lameEauRow.createCell(indexColumn);
		title3(computeContext, lameEauCell, "Hauteur de lame d'eau");
		Cell lameEauCell2 = lameEauRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, lameEauCell2, "(m)");
		makeRightTopBorder(lameEauCell2);

		rowIndexExutoire++;

		Row revancheRow = sheet.createRow(rowIndexExutoire);
		Cell revancheCell = revancheRow.createCell(indexColumn);
		title3(computeContext, revancheCell, "Revanche (m)");
		Cell revancheCell2 = revancheRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, revancheCell2, "(m)");
		makeRightBorder(revancheCell2);

		rowIndexExutoire++;

		Row evacuateurRow = sheet.createRow(rowIndexExutoire);
		Cell evacuateurCell = evacuateurRow.createCell(indexColumn);
		title3(computeContext, evacuateurCell, "L:  Largeur de l'évacuateur (m)");
		Cell evacuateurCell2 = evacuateurRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, evacuateurCell2, "L déversoir (m)");
		makeRightBorder(evacuateurCell2);

		rowIndexExutoire++;

		Row seuilRow = sheet.createRow(rowIndexExutoire);
		Cell seuilCell = seuilRow.createCell(indexColumn);
		title3(computeContext, seuilCell, "H:  Hauteur de la charge sur le seuil (lame d'eau (m))");
		Cell seuilCell2 = seuilRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, seuilCell2, "H déversoir (m)");

		rowIndexExutoire++;
		Row blankRow1 = sheet.createRow(rowIndexExutoire);
		blankRow1.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));
		rowIndexExutoire++;

		Row dimResumeRow = sheet.createRow(rowIndexExutoire);
		CellRangeAddress range = new CellRangeAddress(rowIndexExutoire, rowIndexExutoire+1, indexColumn, indexColumn);
		XlsUtils.mergeCol(computeContext, sheet, indexColumn, rowIndexExutoire, rowIndexExutoire + 1);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM_DASH_DOT, range, sheet);
		Cell dimResumeCell = dimResumeRow.createCell(indexColumn);
		title3(computeContext, dimResumeCell, "Dimensions de la zone de passage de l'eau (m) :");
	

		Cell dimResumeLCell2 = dimResumeRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeLCell2, "Largeur (m)");

		rowIndexExutoire++;

		Row dimResumeHRow2 = sheet.createRow(rowIndexExutoire);
		Cell dimResumeHCell2 = dimResumeHRow2.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeHCell2, "Hauteur (m)");
		makeRightBorder(dimResumeHCell2);

		rowIndexExutoire++;

		Row rBlank2 = sheet.createRow(rowIndexExutoire);
		rBlank2.setHeight((short) (30));
		makeBottomBorder(sheet, rowIndexExutoire, rowIndexExutoire, 0, nbOuvrage + 1);
				
		rowIndexExutoire++;
		
		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		log.info("Génération d'un lot de creeks, génération des autres colonnes");
		for (Creek c : creeks) {

			log.info("génération du creek " + c.getNom());
			if (c.getExutoires().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, creekRow.getRowNum(), indexColumn,
						indexColumn + c.exutoires.size() - 1);
			}
			Cell creekCell = creekRow.createCell(indexColumn);
			backBlueBoldBorderTopLeftRight(computeContext, creekCell, c.nom);
			makeBoldBorder(sheet, creekRow.getRowNum(), creekRow.getRowNum(), creekCell.getColumnIndex(),
					creekCell.getColumnIndex() + c.exutoires.size() - 1);
			makeBoldBorder(creekCell);
			
			int nbE = c.getExutoires().size();
			int currentE = 0;

			for (BVExutoire e : c.getExutoires()) {

				currentE++;

				Cell exuNomCell = exutoireRow.createCell(indexColumn);
				redBoldBorderLeftRight(computeContext, exuNomCell, e.getNom());
				if (nbE == 1) {
					makeAllBoldExceptBottomBorder(exuNomCell);
				} else {
					if (currentE == 1) {
						makeLeftTopBorder(exuNomCell);
					} else if (currentE == nbE) {
						makeRightTopBorder(exuNomCell);
					} else {
						makeTopBorder(exuNomCell);
					}
				}

				Cell exuSurfCell = superficieRow.createCell(indexColumn);
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 10000);
				if (nbE == 1) {
					makeLeftRightBorder(exuSurfCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(exuSurfCell);
					} else if (currentE == nbE) {
						makeRightBorder(exuSurfCell);
					}
				}

				Cell lgHydroCell = longueurRow.createCell(indexColumn);
				standardCellDecimalNoComma(computeContext, lgHydroCell, "")
						.setCellValue(e.getLongueurHydro().intValue());
				if (nbE == 1) {
					makeLeftRightBorder(lgHydroCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(lgHydroCell);
					} else if (currentE == nbE) {
						makeRightBorder(lgHydroCell);
					}
				}

				Cell deniveleCell = deniveleRow.createCell(indexColumn);
				standardCellDecimalNoComma(computeContext, deniveleCell, "").setCellValue(e.getDenivele().intValue());
				if (nbE == 1) {
					makeLeftRightBorder(deniveleCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(deniveleCell);
					} else if (currentE == nbE) {
						makeRightBorder(deniveleCell);
					}
				}

				Cell penteCell = penteRow.createCell(indexColumn);
				String penteFormula = String.format("(%s%s/%s%s)*100",
						CellReference.convertNumToColString(deniveleCell.getColumnIndex()),
						deniveleCell.getRowIndex() + 1,
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1);
				standardCellDecimalNoComma(computeContext, penteCell, "").setCellFormula(penteFormula);
				if (nbE == 1) {
					makeLeftRightBorder(penteCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(penteCell);
					} else if (currentE == nbE) {
						makeRightBorder(penteCell);
					}
				}

				Cell ruissellementCell = ruissellementRow.createCell(indexColumn);
				standardCell(computeContext, ruissellementCell, "")
						.setCellFormula(parametresGenerator.parametres.get(ParametresGenerator.CST_COEFF_RUISS_PARAM));
				if (nbE == 1) {
					makeLeftRightBorder(ruissellementCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(ruissellementCell);
					} else if (currentE == nbE) {
						makeRightBorder(ruissellementCell);
					}
				}

				Cell ecoulementCell = ecoulementRow.createCell(indexColumn);
				String ecoulementFormula = String.format("IF(%s%s<5,\"1\", IF(%s%s>15, \"4\", \"2\"))",
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1,
						CellReference.convertNumToColString(penteCell.getColumnIndex()), penteCell.getRowIndex() + 1);
				standardCell(computeContext, ecoulementCell, "").setCellFormula(ecoulementFormula);
				if (nbE == 1) {
					makeLeftRightBorder(ecoulementCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(ecoulementCell);
					} else if (currentE == nbE) {
						makeRightBorder(ecoulementCell);
					}
				}

				Cell calculTpsConcCell = calculTpsConcentrationRow.createCell(indexColumn);
				String calculTpsConcFormula = String.format("%s%s/%s%s/60",
						CellReference.convertNumToColString(lgHydroCell.getColumnIndex()),
						lgHydroCell.getRowIndex() + 1,
						CellReference.convertNumToColString(ecoulementCell.getColumnIndex()),
						ecoulementCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculTpsConcCell, "").setCellFormula(calculTpsConcFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculTpsConcCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculTpsConcCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculTpsConcCell);
					}
				}

				Cell tpsConcRetenuCell = tpsConcentrationRetenuRow.createCell(indexColumn);
				String tpsConcRetenuFormula = String.format("IF(%s%s>%s,%s%s, %s)",
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM),
						CellReference.convertNumToColString(calculTpsConcCell.getColumnIndex()),
						calculTpsConcCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_TPS_CONCENTRATION_PARAM));
				standardCell(computeContext, tpsConcRetenuCell, "").setCellFormula(tpsConcRetenuFormula);
				if (nbE == 1) {
					makeLeftRightBorder(tpsConcRetenuCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(tpsConcRetenuCell);
					} else if (currentE == nbE) {
						makeRightBorder(tpsConcRetenuCell);
					}
				}

				Cell calculAverseCell = intensiteAverseRow.createCell(indexColumn);
				String calculAverseFormula = String.format("%s*(%s%s^-%s)",
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_A_PARAM),
						CellReference.convertNumToColString(tpsConcRetenuCell.getColumnIndex()),
						tpsConcRetenuCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.METEO_COEFF_MONTANA_B_PARAM));
				standardCellDecimal2Comma(computeContext, calculAverseCell, "").setCellFormula(calculAverseFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculAverseCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculAverseCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculAverseCell);
					}
				}

				Cell calculDebitCell = calculDebitRow.createCell(indexColumn);
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s*0.01)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal1Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);
				if (nbE == 1) {
					makeLeftRightBottomBorder(calculDebitCell);
				} else {
					if (currentE == 1) {
						makeLeftBottomBorder(calculDebitCell);
					} else if (currentE == nbE) {
						makeRightBottomBorder(calculDebitCell);
					} else {
						makeBottomBorder(calculDebitCell);
					}
				}

				Cell calculHauteurLameEauCell = lameEauRow.createCell(indexColumn);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);
				if (nbE == 1) {
					makeAllBoldExceptBottomBorder(calculHauteurLameEauCell);
				} else {
					if (currentE == 1) {
						makeLeftTopBorder(calculHauteurLameEauCell);
					} else if (currentE == nbE) {
						makeRightTopBorder(calculHauteurLameEauCell);
					} else {
						makeTopBorder(calculHauteurLameEauCell);
					}
				}

				Cell calculRevancheCell = revancheRow.createCell(indexColumn);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCellDecimal2Comma(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculRevancheCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculRevancheCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculRevancheCell);
					}
				}

				Cell calculLargeurEvacuateurCell = evacuateurRow.createCell(indexColumn);
				String calculLargeurEvacuateurFormula = String.format("%s%s/(%s*POWER(2*%s,0.5)*POWER(%s%s,3/2))",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_N_PARAM),
						parametresGenerator.parametres.get(ParametresGenerator.CST_G_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculLargeurEvacuateurCell, "")
						.setCellFormula(calculLargeurEvacuateurFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculLargeurEvacuateurCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculLargeurEvacuateurCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculLargeurEvacuateurCell);
					}
				}

				Cell calculHChargeSeuilCell = seuilRow.createCell(indexColumn);
				String calculHChargeSeuilFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHChargeSeuilCell, "")
						.setCellFormula(calculHChargeSeuilFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculHChargeSeuilCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculHChargeSeuilCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculHChargeSeuilCell);
					}
				}

				Cell calculDimResumeLCell = dimResumeRow.createCell(indexColumn);
				String calculDimResumeLFormula = String.format("MROUND(%s%s+0.25,0.5)",
						CellReference.convertNumToColString(calculLargeurEvacuateurCell.getColumnIndex()),
						calculLargeurEvacuateurCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeLCell, "")
						.setCellFormula(calculDimResumeLFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculDimResumeLCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculDimResumeLCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculDimResumeLCell);
					}
				}

				Cell calculDimResumeHCell = dimResumeHRow2.createCell(indexColumn);
				String calculDimResumeHFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculHChargeSeuilCell.getColumnIndex()),
						calculHChargeSeuilCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculDimResumeHCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculDimResumeHCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculDimResumeHCell);
					} else {
//						makeBottomBorder(calculDimResumeHCell);
					}
				}

				indexColumn++;

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.Q100_SHEET_PROGRESS, (int) progress);
			}
		}

//		XlsUtils.makeBoldBorder(sheet, firstRow+14, firstRow+15, 1, indexColumn-1);
//		
//		XlsUtils.makeBoldBorder(sheet, firstRow+14, firstRow+15, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, firstRow+1, rowIndexExutoire-1, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, firstRow, firstRow+1, 3, indexColumn-1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow+1, rowIndexExutoire-1, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexExutoire-7, rowIndexExutoire-6, 1, indexColumn-1);
//		XlsUtils.makeBoldBorder(sheet, rowIndexExutoire-2, rowIndexExutoire, 1, indexColumn-1);

//        XlsUtils.makeBoldBorder(sheet, firstRow + 3, firstRow + 13, 0, 1);
//        XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 22, 0, 1);
//
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 13, 0, indexColumn - 1);
//
//        XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 15, 0, indexColumn - 1);
//
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, 1);
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, indexColumn - 1);
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, indexColumn - 1);
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 2, indexColumn - 1);
//        XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 1, 2, indexColumn - 1);

//		XlsUtils.makeBoldBorder(sheet, firstRow - 1, firstRow + 22, 1, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow - 1, firstRow, 3, indexColumn - 1);

	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		// une colonne vide
		int indexColumn = 0;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Caractéristiques des bassins versants d'exutoires et débits associés à l'état initial ";
		Cell headerCell = titleRow.createCell(indexColumn);
		title1(computeContext, headerCell, title).setCellFormula("CONCATENATE(\"" + title + "\","
				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexExutoire++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}
}
