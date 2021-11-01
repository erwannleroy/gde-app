package org.r1.gde.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GDEProcess {

	private File bvFile;
	private File decanteursFile;
	private List<BVDecanteur> bassins = new ArrayList<>();
	private Map<String, Object> map;
	private Workbook workbook = new XSSFWorkbook();


	public GDEProcess(File bvFile, File decanteursFile) {
		this.bvFile = bvFile;
		this.decanteursFile = decanteursFile;
	}

	public void start() {
//		try {
////			parseBV();
////
////			generateDimensionnementSheet();
////
////			writeToFile();
//		} catch (IOException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	

	
	


}
