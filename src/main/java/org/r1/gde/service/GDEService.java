package org.r1.gde.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.r1.gde.controller.BVResponse;
import org.r1.gde.controller.DECResponse;
import org.r1.gde.demo.FileStorageService;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Performance;
import org.r1.gde.model.TypeDecanteur;
import org.r1.gde.model.decanteur.Decanteur;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.xls.generator.ParametresGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;

@Service
@Slf4j
public class GDEService {

	@Autowired
	private GDEComputer gdeComputer;

	@Autowired
	private FileStorageService fileStorageService;

	public BVResponse giveBVFile(MultipartFile file) {

		log.debug("giveBVFile");

		String storeFile = this.fileStorageService.storeFile(file);

		BVResponse result = new BVResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<BassinVersant> bassins = parseBV(resource.getFile());
				result.setFileFormatOk(true);
				result.setNbBassins(bassins.size());
				gdeComputer.updateBassins(bassins);
			} catch (IOException | ParseException e) {
				log.error("Impossible de parser le fichier BV", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getCause().getMessage());
				result.setError(true);
			}
		}
		return result;
	}

	public DECResponse giveDECFile(MultipartFile file) {

		log.debug("giveDECFile");

		String storeFile = this.fileStorageService.storeFile(file);

		DECResponse result = new DECResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<Zone> zones = parseDEC(resource.getFile());
				result.setFileFormatOk(true);
				result.setNbZone(zones.size());
				gdeComputer.updateDecanteurs(zones);
			} catch (IOException | ParseException e) {
				log.error("Impossible de parser le fichier DEC", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getCause().getMessage());
				result.setError(true);
			}
		}
		return result;
	}



	public BVResponse giveBVFilePath(String bvFilePath) {
		log.debug("giveBVFile {}", bvFilePath);
		BVResponse result = new BVResponse();
		File bvFile = new File(bvFilePath);

		if (bvFile.exists()) {
			result.setFileExists(true);
			try {
				List<BassinVersant> bassins = parseBV(bvFile);
				result.setFileFormatOk(true);
				result.setNbBassins(bassins.size());
				gdeComputer.updateBassins(bassins);
			} catch (IOException | ParseException e) {
				log.error("Impossible de parser le fichier BV", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getCause().getMessage());
				result.setError(true);
			}
		}
		return result;
	}

	private List<Zone> parseDEC(File decfile) throws IOException, ParseException {
		List<Zone> zones = new ArrayList<Zone>();
		TempDecanteursParsing tempResult = new TempDecanteursParsing();

		log.debug("Parsing du bv " + decfile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		InputStream dbf = new FileInputStream(decfile);

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				tempResult.addDecanteur(recordDECToDecanteur(rec));
			}
		}
		log.debug("Parcours de " + tempResult.zones.size() + " zones");
//		log.debug("Decanteurs:" + decanteurs);
		return tempResult.getZones();
	}
	
	private List<BassinVersant> parseBV(File bvFile) throws IOException, ParseException {
		List<BassinVersant> bassins = new ArrayList<BassinVersant>();

		log.debug("Parsing du bv " + bvFile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		InputStream dbf = new FileInputStream(bvFile);

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				bassins.add(recordBVToBassinVersant(rec));
			}
		}
		log.debug("Parcours de " + bassins.size() + " bassins versants");
		log.debug("Bassins:" + bassins);
		return bassins;
	}

	private BassinVersant recordBVToBassinVersant(DbfRecord rec) throws ParseException {
		BassinVersant bv = new BassinVersant();
		Map<String, Object> map = rec.toMap();
		Object nomField = map.get(BassinVersant.NOM_OUVRAGE_FIELD);
		bv.setNomOuvrage(nomField != null ? nomField.toString() : "");
		Object deniveleField = map.get(BassinVersant.DENIVELE_FIELD);
		bv.setDenivele(deniveleField !=null ? Integer.parseInt(deniveleField.toString()) : null);
		Object longueurField = map.get(BassinVersant.LONGUEUR_FIELD);
		bv.setLongueur(longueurField !=null ? Integer.parseInt(longueurField.toString()) : null);
		Object surfaceField = map.get(BassinVersant.SURFACE_FIELD);
		bv.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		bv.setPerformance(Performance.toPerformance(map.get(BassinVersant.PERFORMANCE_FIELD).toString()));
		return bv;
	}
	
	private Decanteur recordDECToDecanteur(DbfRecord rec) throws ParseException {
		Decanteur dec = new Decanteur();
		Map<String, Object> map = rec.toMap();
		Object nomField = map.get(Decanteur.NOM_FIELD);
		dec.setNom(nomField != null ? nomField.toString() : "");
		Object surfaceField = map.get(Decanteur.SURFACE_FIELD);
		dec.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		Object profondeurField = map.get(Decanteur.PROFONDEUR_FIELD);
		dec.setProfondeur(profondeurField !=null ? Double.parseDouble(profondeurField.toString()) : null);
		Object profondeurDeversoirField = map.get(Decanteur.PROFONDEUR_DEVERSOIR_FIELD);
		dec.setProfondeurDeversoir(profondeurDeversoirField!=null ? Double.parseDouble(profondeurDeversoirField.toString()) : null);
		Object hauteurDigueField = map.get(Decanteur.HAUTEUR_DIGUE_FIELD);
		dec.setHauteurDigue(hauteurDigueField!=null ? Double.parseDouble(hauteurDigueField.toString()) : null);
		dec.setType(TypeDecanteur.toTypeDecanteur(map.get(Decanteur.TYPE_FIELD).toString()));
		Object bvField = map.get(Decanteur.BV_FIELD);
		List<String> bvList = Arrays.asList("Tp_B1", "Tp_B2", "Tp_B3", "Tp_B4", "Tp_B5", "Tp_B6");
	    Collections.shuffle(bvList);
	    dec.setBv(bvList.get(0));
		List<String> zoneList = Arrays.asList("zone 1", "zone 2", "zone 3", "zone 4", "zone 5", "zone 6");
	    Collections.shuffle(zoneList);
		dec.setZone(zoneList.get(0));
//		Object zoneField = map.get(Decanteur.ZONE_FIELD);
//		dec.setZone(zoneField != null ? zoneField.toString() : "");
		return dec;
	}

}
