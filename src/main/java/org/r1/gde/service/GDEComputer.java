package org.r1.gde.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.xls.generator.CassisGenerator;
import org.r1.gde.xls.generator.ObjectifsRetentionGenerator;
import org.r1.gde.xls.generator.Q100Generator;
import org.r1.gde.xls.generator.ParametresGenerator;
import org.r1.gde.xls.generator.RetentionGenerator;
import org.r1.gde.xls.generator.SheetGeneratorEvent;
import org.r1.gde.xls.generator.SheetGeneratorListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GDEComputer implements SheetGeneratorListener {

	@Autowired
	private ParametresGenerator parametresGenerator;
	@Autowired
	private ObjectifsRetentionGenerator dimensionnementGenerator;
	@Autowired
	private RetentionGenerator retentionGenerator;
	@Autowired
	private Q100Generator q100Generator;
	@Autowired
	private CassisGenerator cassisGenerator;

	private List<BVDecanteur> bassins;
	private List<Zone> zones;
	private List<Creek> creeks;

	private ComputeContext computeContext = new ComputeContext();

	private boolean fileTestEnabled = true;

	private XSSFWorkbook workbook() {
		return this.computeContext.workbook;
	}

	public ComputeContext getComputeContext() {
		return this.computeContext;
	}

	@PostConstruct
	public void init() {
		dimensionnementGenerator.addListener(this);
		retentionGenerator.addListener(this);
		cassisGenerator.addListener(this);
		q100Generator.addListener(this);
		reset();
	}

	public void reset() {
		this.computeContext = new ComputeContext();
		parametresGenerator.generateSheet(computeContext);
	}

	public void updateBassins(List<BVDecanteur> bassins) {
		computeContext.getComputingResult().setInProgress(true);
		computeContext.getComputingResult().setComputationOk(false);
		this.bassins = bassins;
		this.computeContext.setBassins(bassins);
		this.computeContext.getComputingResult().setBvDecSent(true);
		dimensionnementGenerator.generateSheet(computeContext);
//		fillBytes();
	}

	private void fillBytes() {
		try {
			ByteArrayOutputStream bytes = writeBytes();
			// String bytesString =
			// Base64.getEncoder().encodeToString(bytes.toByteArray());
			computeContext.getComputingResult().setXls(bytes.toByteArray());
			bytes.flush();
			bytes.close();
			computeContext.getComputingResult().setInProgress(false);
			computeContext.getComputingResult().setComputationOk(true);
			log.info("Génération du résultat terminé");
		} catch (IOException e) {
			computeContext.getComputingResult().setInProgress(false);
			computeContext.getComputingResult().setComputationOk(false);
			log.error("Impossible de générer le fichier Excel", e);
		}
	}

	private ByteArrayOutputStream writeBytes() throws IOException {
		log.info("Ecriture du fichier Excel");
		Path temp = Files.createTempFile("", "temp.xlsx");
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

//		if (this.fileTestEnabled) {
//			log.info("Ecriture du fichier : " + temp.getFileName());
//			FileOutputStream file = new FileOutputStream(temp.toFile());
//			workbook().write(file);
//			file.close();
//		}
		XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook());
		workbook().write(bytes);
		bytes.close();
//		workbook().close();
		return bytes;
	}

	public void updateDecanteurs(List<Zone> zones) {
		this.zones = zones;
		computeContext.getComputingResult().setInProgress(true);
		computeContext.getComputingResult().setComputationOk(false);
		this.computeContext.setZones(zones);
		this.computeContext.getComputingResult().setDecSent(true);
		retentionGenerator.generateSheet(computeContext);
//		fillBytes();
	}

	public void updateExutoires(List<Creek> creeks) {
		log.info("gdeComputer updateExutoire");
		this.creeks = creeks;
		computeContext.getComputingResult().setInProgress(true);
		computeContext.getComputingResult().setComputationOk(false);
		this.computeContext.setCreeks(creeks);
		this.computeContext.getComputingResult().setBvExuSent(true);
		q100Generator.generateSheet(computeContext);
//		fillBytes();
	}

	@Override
	public void notifyEvent(SheetGeneratorEvent e, Object value) {
		switch (e) {
		case Q100_SHEET_PROGRESS:
			this.computeContext.getComputingResult().setQ100InProgress((int) value);
			break;
		case RETENTION_SHEET_PROGRESS:
			this.computeContext.getComputingResult().setRetentionInProgress((int) value);
			break;
		case OBJECTIFS_SHEET_PROGRESS:
			this.computeContext.getComputingResult().setObjectifsInProgress((int) value);
			break;
		case CASSIS_SHEET_PROGRESS:
			this.computeContext.getComputingResult().setCassisInProgress((int) value);
			break;
		case Q100_SHEET_GENERATED:
			this.computeContext.getComputingResult().setQ100Genere(true);
			this.computeContext.getComputingResult().setQ100Computing(false);
			cassisGenerator.generateSheet(computeContext);
			break;
		case CASSIS_SHEET_GENERATED:
			this.computeContext.getComputingResult().setCassisGenere(true);
			this.computeContext.getComputingResult().setCassisComputing(false);
			fillBytes();
			break;
		case OBJECTIFS_SHEET_GENERATED:
			this.computeContext.getComputingResult().setObjectifsGenere(true);
			this.computeContext.getComputingResult().setObjectifsComputing(false);
			fillBytes();
			break;
		case RETENTION_SHEET_GENERATED:
			this.computeContext.getComputingResult().setRetentionGenere(true);
			this.computeContext.getComputingResult().setRetentionComputing(false);
			fillBytes();
			break;
		}
	}
}
