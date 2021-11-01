package org.r1.gde.xls.generator;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.service.ComputeContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SheetGenerator {

	public ComputeContext computeContext;

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

	public void generateSheet(ComputeContext computeContext) {
		this.computeContext = computeContext;
		try {
			startGeneration();
		} catch (GDEException e) {
			log.error("Impossible de générer", e);
		}
	}

	protected abstract void startGeneration() throws GDEException;
	
	public abstract String getTitleSheet();
}
