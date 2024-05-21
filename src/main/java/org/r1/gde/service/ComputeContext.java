package org.r1.gde.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.springframework.web.multipart.MultipartFile;

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
	private BytesResult bytesResult;
	Map<String, Double> performanceBVDecanteur;
	Map<String, Double> debitBVExutoire;
	
	File bvDecFile;
	File exuFile;

	public ComputeContext() {
		this.workbook = new XSSFWorkbook();
		this.computingResult = new ComputingResult();
		this.bytesResult = new BytesResult();
		this.performanceBVDecanteur = new HashMap<String, Double>();
		this.debitBVExutoire = new HashMap<String, Double>();
	}


}
