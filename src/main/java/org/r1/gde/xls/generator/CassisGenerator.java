package org.r1.gde.xls.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
//import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
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
public class CassisGenerator extends SheetGenerator {

	private int rowIndexExutoire = 0;
	private static final String TITLE_SHEET = "Dimensionnement cassis fossés";
	private static final int TAILLE_LOT = 30;
	private int nbOuvragesTraites = 0;
	private int nbOuvragesTotal = 0;

	@Autowired
	ParametresGenerator parametresGenerator;

	public void run() {
		log.info("Génération de l'onglet Cassis");

		this.computeContext.getComputingResult().setCassisComputing(true);

		sheet = workbook().getSheet(TITLE_SHEET);

		if (null != sheet) {
			workbook().removeSheetAt(workbook().getSheetIndex(sheet));
		}

		sheet = workbook().createSheet(TITLE_SHEET);
		super.setup();

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
		notifyListeners(SheetGeneratorEvent.CASSIS_SHEET_GENERATED, null);
	}

	private void generateCreeks() {

		List<Creek> remainList = Lists.newArrayList(creeks().iterator());
		List<List<Creek>> sharedCreeks = new ArrayList<List<Creek>>();

		log.info("Partage des creeks - debut");
		sharedCreeks = shareCreeks(sharedCreeks, remainList, new ArrayList<Creek>());
		log.info("Partage des creeks - debut");

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

	private void generateLotCreek(List<Creek> creeks) {

		int nbOuvrage = 0;
		for (Creek c : creeks) {
			nbOuvrage += c.exutoires.size();
		}

		// on commence au bord
		int indexColumn = 0;

		rowIndexExutoire++;

		int firstRow = rowIndexExutoire;

		rowIndexExutoire++;

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
		title3LeftBorder(computeContext, titleSuperficie, "Superficie BV (km²) :");
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
		Cell titleCalculDebitCol2 = calculDebitRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, titleCalculDebitCol2, "Q pointe =");

		rowIndexExutoire++;

		// une ligne vide
		Row blankRow = sheet.createRow(rowIndexExutoire);
		blankRow.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));

		rowIndexExutoire++;

		Row title2Row = sheet.createRow(rowIndexExutoire);
//		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, nbOuvrage + 1);
		title2Row.setRowStyle(XlsUtils.blankRow(computeContext));

		String title = "Sections des fossés et cassis";
		Cell headerCell = title2Row.createCell(indexColumn);
		XlsUtils.title2(computeContext, headerCell, title);
		makeBoldBorder(headerCell);

		rowIndexExutoire++;

		Row desc2Row = sheet.createRow(rowIndexExutoire);
		float hRow = 28;
		desc2Row.setHeightInPoints(hRow);
		XlsUtils.mergeRow(computeContext, sheet, rowIndexExutoire, indexColumn, nbOuvrage + 1);
		desc2Row.setRowStyle(XlsUtils.blankRow(computeContext));
		String description = "Le cassis est assimilé à un fossé rectangulaire.\nApproximation par section rectangulaire et formules de Manning-Strickler / Chezy   (comparaison des 2 membres de la formule)";
		Cell descCell = desc2Row.createCell(indexColumn);
		XlsUtils.subTitleCell(computeContext, descCell, description);
		makeBoldBorder(sheet, rowIndexExutoire, rowIndexExutoire, indexColumn, nbOuvrage + 1);
		rowIndexExutoire++;

		Row penteFosseRow = sheet.createRow(rowIndexExutoire);
		Cell penteFosseCell = penteFosseRow.createCell(indexColumn);
		title3(computeContext, penteFosseCell, "Pente fossé-cassis (m/m)");

		rowIndexExutoire++;

		Row coefStricklerRow = sheet.createRow(rowIndexExutoire);
		Cell coefStricklerCell = coefStricklerRow.createCell(indexColumn);
		title3(computeContext, coefStricklerCell, "Coef. rugosié Strickler (Ks) lié à fossé-cassis");

		Row lameEauRow = sheet.createRow(rowIndexExutoire);
		Cell lameEauCell = lameEauRow.createCell(indexColumn);
		title3(computeContext, lameEauCell, "Hauteur de lame d'eau");
		Cell lameEauCell2 = lameEauRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, lameEauCell2, "(m)");

		rowIndexExutoire++;

		Row revancheRow = sheet.createRow(rowIndexExutoire);
		Cell revancheCell = revancheRow.createCell(indexColumn);
		title3(computeContext, revancheCell, "Revanche (m)");
		Cell revancheCell2 = revancheRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, revancheCell2, "(m)");

		rowIndexExutoire++;

		Row largeurFosseRow = sheet.createRow(rowIndexExutoire);
		Cell largeurFosseCell = largeurFosseRow.createCell(indexColumn);
		title3(computeContext, largeurFosseCell, "L:  Largeur du fossé-cassis (m)");

		rowIndexExutoire++;

		Row hauteurFosseRow = sheet.createRow(rowIndexExutoire);
		Cell hauteurFosseCell = hauteurFosseRow.createCell(indexColumn);
		title3(computeContext, hauteurFosseCell, "H:  Hauteur du fossé-cassis");

		rowIndexExutoire++;

		Row blankRow1 = sheet.createRow(rowIndexExutoire);
		blankRow1.setHeight((short) (DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT / 2));

		rowIndexExutoire++;

		Row premierMembreRow = sheet.createRow(rowIndexExutoire);
		Cell premierMembreCell = premierMembreRow.createCell(indexColumn);
		title3(computeContext, premierMembreCell, "Valeur du 1er membre :  (Qp/(Ks*Pmoy0.5))3/2");

		rowIndexExutoire++;

		Row deuxiemeMembreRow = sheet.createRow(rowIndexExutoire);
		Cell deuxiemeMembreCell = deuxiemeMembreRow.createCell(indexColumn);
		title3(computeContext, deuxiemeMembreCell, "Valeur du 2ème membre :  (yL)5/2/(2y+L)");

		rowIndexExutoire++;

		Row dimResumeRow = sheet.createRow(rowIndexExutoire);
		Cell dimResumeCell = dimResumeRow.createCell(indexColumn);
		title3(computeContext, dimResumeCell, "Dimensions retenues");
		XlsUtils.mergeCol(computeContext, sheet, indexColumn, rowIndexExutoire, rowIndexExutoire + 1);

		Cell dimResumeLCell2 = dimResumeRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeLCell2, "Largeur (m)");
		rowIndexExutoire++;
		Row dimResumeHRow2 = sheet.createRow(rowIndexExutoire);
		Cell dimResumeHCell2 = dimResumeHRow2.createCell(indexColumn + 1);
		title3RightBorder(computeContext, dimResumeHCell2, "Hauteur (m)");

		rowIndexExutoire++;

		Row vitMaxRow = sheet.createRow(rowIndexExutoire);
		Cell vitMaxCell = vitMaxRow.createCell(indexColumn);
		title3(computeContext, vitMaxCell, "Vitesse max. dans fossé-cassis (m/s)");
//		makeBottomBorder(vitMaxCell);
		Cell vitMaxCol2 = vitMaxRow.createCell(indexColumn + 1);
		title3RightBorder(computeContext, vitMaxCol2, "V max (m/s)");
		makeRightBorder(vitMaxCol2);

		rowIndexExutoire++;

		Row rBlank2 = sheet.createRow(rowIndexExutoire);
		rBlank2.setHeight((short) (30));
		makeBottomBorder(sheet, rowIndexExutoire, rowIndexExutoire, 0, nbOuvrage + 1);

		rowIndexExutoire++;

		// on décalle de deux colonnes
		indexColumn++;
		indexColumn++;

		log.info("Génération des creeks - debut");
		for (Creek c : creeks) {
			log.info("Génération du creek " + c.nom + " - debut");
			Cell creekCell = creekRow.createCell(indexColumn);
			backBlueBoldBorderTopLeftRight(computeContext, creekCell, c.nom);
			if (c.getExutoires().size() > 1) {
				XlsUtils.mergeRow(computeContext, sheet, creekRow.getRowNum(), creekCell.getColumnIndex(),
						creekCell.getColumnIndex() + c.exutoires.size() - 1);
			}
			makeBoldBorder(sheet, creekRow.getRowNum(), creekRow.getRowNum(), creekCell.getColumnIndex(),
					creekCell.getColumnIndex() + c.exutoires.size() - 1);
			makeBoldBorder(creekCell);

			int nbE = c.getExutoires().size();
			int currentE = 0;

			log.info("Génération des exutoires - debut");
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
				standardCellDecimal2Comma(computeContext, exuSurfCell, "").setCellValue(e.getSurface() / 1000000);
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
				standardCell(computeContext, lgHydroCell, "").setCellValue(e.getLongueurHydro().intValue());
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
				standardCell(computeContext, deniveleCell, "").setCellValue(e.getDenivele());
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
				String calculDebitFormula = String.format("(%s%s*%s%S*%s%s)/3.6",
						CellReference.convertNumToColString(ruissellementCell.getColumnIndex()),
						ruissellementCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculAverseCell.getColumnIndex()),
						calculAverseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(exuSurfCell.getColumnIndex()),
						exuSurfCell.getRowIndex() + 1);
				standardCellDecimal1Comma(computeContext, calculDebitCell, "").setCellFormula(calculDebitFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculDebitCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculDebitCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculDebitCell);
					}
				}

				Cell calculPenteFosseCell = penteFosseRow.createCell(indexColumn);
				String penteFosseFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.CST_PENTE_PARAM));
				standardCell(computeContext, calculPenteFosseCell, "").setCellFormula(penteFosseFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculPenteFosseCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculPenteFosseCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculPenteFosseCell);
					}
				}

				Cell calculHauteurLameEauCell = lameEauRow.createCell(indexColumn);
				String calculHauteurLameEauFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_H_LAME_EAU_PARAM));
				standardCell(computeContext, calculHauteurLameEauCell, "").setCellFormula(calculHauteurLameEauFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculHauteurLameEauCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculHauteurLameEauCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculHauteurLameEauCell);
					}
				}

				Cell calculRevancheCell = revancheRow.createCell(indexColumn);
				String calculRevancheFormula = String.format("%s",
						parametresGenerator.parametres.get(ParametresGenerator.OUVRAGE_REVANCHE_PARAM));
				standardCell(computeContext, calculRevancheCell, "").setCellFormula(calculRevancheFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculRevancheCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculRevancheCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculRevancheCell);
					}
				}

				Cell calculLargeurFosseCell = largeurFosseRow.createCell(indexColumn);
				standardCellDecimal2Comma(computeContext, calculLargeurFosseCell, "0.00").setCellValue(0d);
				if (nbE == 1) {
					makeLeftRightBorder(calculLargeurFosseCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculLargeurFosseCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculLargeurFosseCell);
					}
				}

				Cell calculHauteurFosseCell = hauteurFosseRow.createCell(indexColumn);
				String calculHauteurFosseFormula = String.format("%s%s+%s%s",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculRevancheCell.getColumnIndex()),
						calculRevancheCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculHauteurFosseCell, "")
						.setCellFormula(calculHauteurFosseFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculHauteurFosseCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculHauteurFosseCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculHauteurFosseCell);
					}
				}

				Cell calculPremierMembreCell = premierMembreRow.createCell(indexColumn);
				String calculPremierMembreFormula = String.format("POWER(%s%s/(%s*POWER(%s%s,1/2)),3/2)",
						CellReference.convertNumToColString(calculDebitCell.getColumnIndex()),
						calculDebitCell.getRowIndex() + 1,
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEF_STRICKLER_PARAM),
						CellReference.convertNumToColString(calculPenteFosseCell.getColumnIndex()),
						calculPenteFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculPremierMembreCell, "")
						.setCellFormula(calculPremierMembreFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculPremierMembreCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculPremierMembreCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculPremierMembreCell);
					}
				}

				Cell calculDeuxiemeMembreCell = deuxiemeMembreRow.createCell(indexColumn);
				String calculDeuxiemeMembreFormula = String.format("(POWER(%s%s*%s%s,5/2))/(2*%s%s+%s%s)",
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDeuxiemeMembreCell, "")
						.setCellFormula(calculDeuxiemeMembreFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculDeuxiemeMembreCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculDeuxiemeMembreCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculDeuxiemeMembreCell);
					}
				}

				Cell calculDimResumeLCell = dimResumeRow.createCell(indexColumn);
				String calculDimResumeLFormula = String.format("%s%s",
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1);
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
						CellReference.convertNumToColString(calculHauteurFosseCell.getColumnIndex()),
						calculHauteurFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculDimResumeHCell, "")
						.setCellFormula(calculDimResumeHFormula);
				if (nbE == 1) {
					makeLeftRightBorder(calculDimResumeHCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculDimResumeHCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculDimResumeHCell);
					}
				}

				Cell calculVitMaxCell = vitMaxRow.createCell(indexColumn);
				String calculVitMaxFormula = String.format("%s*POWER((%s%s*%s%s)/(2*%s%s+%s%s),2/3)*POWER(%s%s,1/2)",
						parametresGenerator.parametres.get(ParametresGenerator.CST_COEF_STRICKLER_PARAM),
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculHauteurLameEauCell.getColumnIndex()),
						calculHauteurLameEauCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculLargeurFosseCell.getColumnIndex()),
						calculLargeurFosseCell.getRowIndex() + 1,
						CellReference.convertNumToColString(calculPenteFosseCell.getColumnIndex()),
						calculPenteFosseCell.getRowIndex() + 1);
				standardCellDecimal2Comma(computeContext, calculVitMaxCell, "").setCellFormula(calculVitMaxFormula);
				if (nbE == 1) {
					makeLeftRightBottomBorder(calculVitMaxCell);
				} else {
					if (currentE == 1) {
						makeLeftBorder(calculVitMaxCell);
					} else if (currentE == nbE) {
						makeRightBorder(calculVitMaxCell);
					} else {
//						makeBottomBorder(calculVitMaxCell);
					}
				}

				indexColumn++;

				nbOuvragesTraites++;
				double progress = (double) 100 / nbOuvragesTotal * nbOuvragesTraites;
				notifyListeners(SheetGeneratorEvent.CASSIS_SHEET_PROGRESS, (int) progress);
			}
			log.info("Génération des exutoires - fin");
			log.info("Génération du creek " + c.nom + " - fin");
		}

		log.info("Génération des creeks - fin");

		log.info("Génération des bordures - debut");
//		XlsUtils.makeBoldBorder(sheet, firstRow + 3, firstRow + 13, 0, 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 22, 0, 1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 27, 0, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 13, 0, indexColumn - 1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow + 15, firstRow + 15, 0, indexColumn - 1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 22, 0, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 2, 2, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, firstRow + 1, 2, indexColumn - 1);
//
//		XlsUtils.makeBoldBorder(sheet, firstRow + 14, firstRow + 15, 0, indexColumn - 1);
//		XlsUtils.makeBoldBorder(sheet, firstRow + 1, rowIndexExutoire - 1, 0, indexColumn - 1);
		log.info("Génération des bordures - fin");
	}

	private void generateTitleBlock() {
		Row titleRow = sheet.createRow(rowIndexExutoire);

		// une colonne vide
		int indexColumn = 0;

		XlsUtils.mergeRow(computeContext, sheet, 0, indexColumn, TAILLE_LOT + 2);

		titleRow.setRowStyle(XlsUtils.blankRow(computeContext));
		String title = "Dimensionnement des ouvrages de canalisation à créer sur le site ";
		Cell headerCell = titleRow.createCell(indexColumn);
		title1(computeContext, headerCell, title);
//		.setCellFormula("CONCATENATE(\"" + title + "\","
//				+ parametresGenerator.parametres.get(ParametresGenerator.GLO_NOM_MINE_PARAM) + ")");

		rowIndexExutoire++;
	}

	@Override
	public String getTitleSheet() {
		return TITLE_SHEET;
	}

}
