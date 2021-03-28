package org.r1.gde.xls.generator;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.decanteur.Ouvrage;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.model.exutoire.Creek;
import org.r1.gde.service.ComputeContext;

public abstract class SheetGenerator {

	public ComputeContext computeContext;

	public Sheet sheet;

	public List<BassinVersant> bassins() {
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

	public void generateSheet(ComputeContext computeContext) {
		this.computeContext = computeContext;
		startGeneration();
	}

	protected abstract void startGeneration();
	
	public abstract String getTitleSheet();
}
