package org.r1.gde.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ComputeContext {

	public XSSFWorkbook workbook;
	List<BVDecanteur> bassins;
	List<Zone> zones;
	List<Creek> creeks;
	private ComputingResult computingResult;

	public ComputeContext() {
		this.workbook = new XSSFWorkbook();
		this.computingResult = new ComputingResult();
	}

}
