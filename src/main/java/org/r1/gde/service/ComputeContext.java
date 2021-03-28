package org.r1.gde.service;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.decanteur.Ouvrage;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.model.exutoire.Creek;

import lombok.Data;

@Data
public class ComputeContext {

	public XSSFWorkbook workbook;
	List<BassinVersant> bassins;
	List<Zone> zones;
	List<Creek> creeks;
	private ComputingResult computingResult;


	public ComputeContext() {
		this.workbook = new XSSFWorkbook();
		this.computingResult = new ComputingResult();
	}

}
