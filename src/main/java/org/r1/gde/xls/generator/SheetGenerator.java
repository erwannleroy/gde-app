package org.r1.gde.xls.generator;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.service.ComputeContext;

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
			l.notifyEvent(e, value);
		}
	}

	public void generateSheet(ComputeContext computeContext) {
		this.computeContext = computeContext;
		try {
			Thread t = new Thread(this);
			t.start();
		} catch (Exception e) {
			log.error("Impossible de générer", e);
		}
	}

	public abstract String getTitleSheet();


}
