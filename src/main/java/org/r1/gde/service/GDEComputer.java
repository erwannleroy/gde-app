package org.r1.gde.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.decanteur.Decanteur;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.xls.generator.DimensionnementGenerator;
import org.r1.gde.xls.generator.ParametresGenerator;
import org.r1.gde.xls.generator.RetentionGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GDEComputer {

	@Autowired
	private ParametresGenerator parametresGenerator;
	@Autowired
	private DimensionnementGenerator dimensionnementGenerator;
	@Autowired
	private RetentionGenerator retentionGenerator;

	
	private List<BassinVersant> bassins;
	private List<Zone> zones;

	private ComputeContext computeContext;

	private boolean fileTestEnabled = true;

	private XSSFWorkbook workbook() {
		return this.computeContext.workbook;
	}

	public ComputeContext getComputeContext() {
		return this.computeContext;
	}

	public void updateBassins(List<BassinVersant> bassins) {
		this.bassins = bassins;
		recompute();
	}

	private void recompute() {
		log.info("Recalcul du résultat");

		this.computeContext = new ComputeContext(bassins, zones);

		parametresGenerator.generateSheet(computeContext);
		dimensionnementGenerator.generateSheet(computeContext);
		retentionGenerator.generateSheet(computeContext);

		fillBytes();
	}

	private void fillBytes() {
		try {
			ByteArrayOutputStream bytes = writeBytes();
			computeContext.getComputingResult().setInProgress(false);
			computeContext.getComputingResult().setComputationOk(true);
			// String bytesStr ing =
			// Base64.getEncoder().encodeToString(bytes.toByteArray());
			computeContext.getComputingResult().setXls(bytes.toByteArray());
			bytes.flush();
			bytes.close();
			log.info("Génération du résultat terminé");
		} catch (IOException e) {
			computeContext.getComputingResult().setInProgress(false);
			computeContext.getComputingResult().setComputationOk(false);
			log.error("Impossible de générer le fichier Excel", e);
		}
	}

	private ByteArrayOutputStream writeBytes() throws IOException {
		System.out.println("Ecriture du fichier Excel");
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		if (this.fileTestEnabled) {
			log.info("Ecriture du fichier : " + fileLocation);
			FileOutputStream file = new FileOutputStream(fileLocation);
			workbook().write(file);
			file.close();
		}
		workbook().write(bytes);
		bytes.close();
		return bytes;
	}

	public void updateDecanteurs(List<Zone> zones) {
		this.zones = zones;
		recompute();
	}

}
