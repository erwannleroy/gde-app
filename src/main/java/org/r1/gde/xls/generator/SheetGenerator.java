package org.r1.gde.xls.generator;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.service.ComputeContext;
import org.r1.gde.service.ComputingResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SheetGenerator implements Runnable {

	public ComputeContext computeContext;
	public List<SheetGeneratorListener> listeners;

	public Sheet sheet;

	public List<BVDecanteur> bassins() {
		return computeContext.getBassins();
	}

	public List<Zone> zones() {
		return computeContext.getZones();
	}

	public List<Creek> creeks() {
		return computeContext.getCreeks();
	}

	public XSSFWorkbook workbook() {
		return computeContext.getWorkbook();
	}

	public void addListener(SheetGeneratorListener l) {
		if (listeners == null) {
			listeners = new ArrayList<SheetGeneratorListener>();
		}
		listeners.add(l);
	}

	public void notifyListeners(SheetGeneratorEvent e, Object value) {
		log.info("Event Generator " + e.name() + " - " + value);
		for (SheetGeneratorListener l : listeners) {
			try {
				l.notifyEvent(e, value);
			} catch (GenerateSheetException gse) {
				log.error(gse.getMessage());
				computeContext.getComputingResult().setError(true);
				computeContext.getComputingResult().setErrorMsg(gse.getMessage());
				break;
			}
		}
	}

	public void generateSheet(ComputeContext computeContext) throws GenerateSheetException {
		computeContext.getComputingResult().setXlsComputationOk(false);
		this.computeContext = computeContext;
		try {
			Thread t = new Thread(this);
			t.start();
		} catch (Exception e) {
			log.error("Impossible de générer", e);
			GenerateSheetException gse = new GenerateSheetException("", e);
			throw gse;
		}
	}

	public abstract String getTitleSheet();

	protected void processError(Exception e) {
		log.error("Impossible de générer", e);
		computeContext.getComputingResult().setError(true);
		String msg = "L'onglet '" + getTitleSheet() + "' ne peut être généré (détail : " + e.getMessage() + ")";
		computeContext.getComputingResult().setErrorMsg(msg);
		detailError();
	}

	protected abstract void detailError();

	public void run() {
		try {
			doRun();
		} catch (Exception e) {
			processError(e);
		}
	}

	protected abstract void doRun() throws GDEException;

	public void processFormulaError(Cell formulaCell) {
		String formulaErrorMsg = FormulaError.forInt(formulaCell.getErrorCellValue()).getString();
		String errorMsg = "Cellule "+ formulaCell.getAddress().formatAsString()+" en erreur '" + formulaErrorMsg + "'";
		log.warn(errorMsg);
		getListErrors(computeContext.getComputingResult()).add(errorMsg);
	}

	protected abstract List<String> getListErrors(ComputingResult cr);
}
