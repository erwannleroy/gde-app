package org.r1.gde.service;

import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.decanteur.Decanteur;
import org.r1.gde.model.decanteur.Zone;

import lombok.Data;

@Data
public class ComputeContext {

	public XSSFWorkbook workbook;
	List<BassinVersant> bassins;
	List<Zone> zones;
	private ComputingResult computingResult;


	public ComputeContext(List<BassinVersant> bassins, List<Zone> zones) {
		this.workbook = new XSSFWorkbook();
		this.bassins = bassins;
		this.zones = zones;
		this.computingResult = new ComputingResult();
	}

}
