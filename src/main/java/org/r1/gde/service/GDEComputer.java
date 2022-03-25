package org.r1.gde.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.DonneesMeteo;
import org.r1.gde.model.Zone;
import org.r1.gde.xls.generator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GDEComputer implements SheetGeneratorListener, DBFGeneratorListener {

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
    @Autowired
    private DBFGenerator perfDBFGenerator;

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
        perfDBFGenerator.addListener(this);
        reset();
    }

    public void reset() {
        this.computeContext = new ComputeContext();
        parametresGenerator.generateSheet(computeContext);
    }

    public void updateBassins(List<BVDecanteur> bassins) {
        this.computeContext.getComputingResult().setBvDecSent(true);
        this.bassins = bassins;
        this.computeContext.setBassins(bassins);
        dimensionnementGenerator.generateSheet(computeContext);
    }

    private void fillBytes() {
        try {
            ByteArrayOutputStream bytes = writeBytes();
            computeContext.getBytesResult().setBytesXls(bytes.toByteArray());
            bytes.flush();
            bytes.close();
            computeContext.getComputingResult().setBytesXlsInProgress(false);
            computeContext.getComputingResult().setXlsComputationOk(true);
            log.info("Génération du résultat terminé");
        } catch(IOException e) {
            computeContext.getComputingResult().setBytesXlsInProgress(false);
            computeContext.getComputingResult().setXlsComputationOk(false);
            log.error("Impossible de générer le fichier Excel", e);
        }
    }


    private ByteArrayOutputStream writeBytes() throws IOException {
        log.info("Ecriture du fichier Excel");
        Path temp = Files.createTempFile("", "temp.xlsx");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook());
        workbook().write(bytes);
        bytes.close();
        return bytes;
    }

    public void updateDecanteurs(List<Zone> zones) {
        this.computeContext.getComputingResult().setDecSent(true);
        this.zones = zones;
        this.computeContext.setZones(zones);
        retentionGenerator.generateSheet(computeContext);
    }

    public void updateMeteo(DonneesMeteo data) {
        ParametresGenerator.METEO_QTE_MAX_PRECIPITATIONS_DEFAULT = data.getMaxPrecipitations();
        ParametresGenerator.METEO_COEFF_MONTANA_A_DEFAULT = data.getCoefA();
        ParametresGenerator.METEO_COEFF_MONTANA_B_DEFAULT = data.getCoefB();
        parametresGenerator.generateSheet(computeContext);
    }

    public void updateExutoires(List<Creek> creeks) {
        this.computeContext.getComputingResult().setBvExuSent(true);
        log.info("gdeComputer updateExutoire");
        this.creeks = creeks;
        this.computeContext.setCreeks(creeks);
        q100Generator.generateSheet(computeContext);
    }

    @Override
    public void notifyEvent(SheetGeneratorEvent e, Object value) {
        switch(e) {
            case Q100_SHEET_PROGRESS:
                this.computeContext.getComputingResult().setQ100ComputeProgress((int) value);
                break;
            case RETENTION_SHEET_PROGRESS:
                this.computeContext.getComputingResult().setRetComputeProgress((int) value);
                break;
            case OBJECTIFS_SHEET_PROGRESS:
                this.computeContext.getComputingResult().setObjRetComputeProgress((int) value);
                break;
            case CASSIS_SHEET_PROGRESS:
                this.computeContext.getComputingResult().setCassisComputeProgress((int) value);
                break;
            case Q100_SHEET_GENERATED:
                this.computeContext.getComputingResult().setQ100ComputeOk(true);
                cassisGenerator.generateSheet(computeContext);
                break;
            case CASSIS_SHEET_GENERATED:
                this.computeContext.getComputingResult().setCassisComputeOk(true);
                fillBytes();
                break;
            case OBJECTIFS_SHEET_GENERATED:
                this.computeContext.getComputingResult().setObjRetComputeOk(true);
                fillBytes();
                break;
            case RETENTION_SHEET_GENERATED:
                this.computeContext.getComputingResult().setRetComputeOk(true);
                perfDBFGenerator.generateDBF(computeContext);
                fillBytes();
                break;
        }
    }

    @Override
    public void notifyEvent(DBFGeneratorEvent e, Object value) {
        switch(e) {
            case BV_DEC_PROGRESS:
                this.computeContext.getComputingResult().setDbfComputeProgress((int) value);
                break;
            case BV_DEC_GENERATED:
                this.computeContext.getComputingResult().setDbfComputationOk(true);
                break;
        }
    }
}
