package org.r1.gde.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.r1.gde.controller.BVResponse;
import org.r1.gde.controller.DECResponse;
import org.r1.gde.controller.EXUResponse;
import org.r1.gde.demo.FileStorageService;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.decanteur.Ouvrage;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.model.exutoire.Creek;
import org.r1.gde.model.exutoire.Exutoire;
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
				List<Zone> zones = parseOuvrages(resource.getFile());
				result.setFileFormatOk(true);
				fillResult(result, zones);
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

	private void fillResult(DECResponse result, List<Zone> zones) {
		result.setNbZones(zones.size());
		int nbBVs = 0;
		int nbOuvrages = 0;

		for (Zone z : zones) {
			nbBVs += z.getBassins().size();
			for (org.r1.gde.model.decanteur.BassinVersant b : z.getBassins()) {
				nbOuvrages += b.getOuvrages().size();
			}
		}

		result.setNbBVs(nbBVs);
		result.setNbDecanteurs(nbOuvrages);
	}

	public EXUResponse giveEXUFile(MultipartFile file) {

		log.debug("giveEXUFile");

		String storeFile = this.fileStorageService.storeFile(file);

		EXUResponse result = new EXUResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<Creek> creeks = parseExutoires(resource.getFile());
				result.setFileFormatOk(true);
				fillResult(result, creeks);
				gdeComputer.updateExutoires(creeks);
			} catch (IOException | ParseException e) {
				log.error("Impossible de parser le fichier EXU", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getCause().getMessage());
				result.setError(true);
			}
		}
		return result;
	}

	private void fillResult(EXUResponse result, List<Creek> creeks) {

		result.setNbCreeks(creeks.size());
		int nbExutoires = 0;

		for (Creek c : creeks) {
			nbExutoires += c.getExutoires().size();
		}
		result.setNbExutoires(nbExutoires);
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

	private List<Zone> parseOuvrages(File decfile) throws IOException, ParseException {
		List<Zone> zones = new ArrayList<Zone>();
		TempOuvragesParsing tempResult = new TempOuvragesParsing();

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
				Ouvrage ouvrage = recordDBFToOuvrage(rec);
				if (ouvrage != null) {
					tempResult.addOuvrage(ouvrage);
				}
			}
		}
		log.debug("Parcours de " + tempResult.zones.size() + " zones");
//		log.debug("Ouvrage:" + ouvrages);
		return tempResult.getZones();
	}

	private List<Creek> parseExutoires(File exufile) throws IOException, ParseException {
		List<Creek> creekszones = new ArrayList<Creek>();
		TempExutoiresParsing tempResult = new TempExutoiresParsing();

		log.debug("Parsing du exuFile " + exufile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		InputStream dbf = new FileInputStream(exufile);

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				Exutoire exutoire = recordDBFToExutoire(rec);
				if (exutoire != null) {
					tempResult.addExutoire(exutoire);
				}
			}
		}
		log.debug("Parcours de " + tempResult.creeks.size() + " creeks");
		return tempResult.getCreeks();
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
				BassinVersant bv = recordBVToBassinVersant(rec);
				if (bv != null) {
					bassins.add(bv);
				}
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
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		bv.setNomOuvrage(nomField != null ? nomField.toString() : "");
		Object deniveleField = map.get(BassinVersant.DENIVELE_FIELD);
		bv.setDenivele(deniveleField != null ? Integer.parseInt(deniveleField.toString()) : null);
		Object longueurField = map.get(BassinVersant.LONGUEUR_FIELD);
		bv.setLongueur(longueurField != null ? Integer.parseInt(longueurField.toString()) : null);
		Object surfaceField = map.get(BassinVersant.SURFACE_FIELD);
		bv.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		Object typeField = map.get(BassinVersant.TYPE_FIELD);
		if (typeField != null && StringUtils.equalsIgnoreCase("ralentisseur", typeField.toString())) {
			return null;
		}
		return bv;
	}

	private Ouvrage recordDBFToOuvrage(DbfRecord rec) throws ParseException {
		Ouvrage dec = new Ouvrage();
		Map<String, Object> map = rec.toMap();
		Object nomField = map.get(Ouvrage.NOM_FIELD);
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		dec.setNom(nomField != null ? nomField.toString() : "");
		Object surfaceField = map.get(Ouvrage.SURFACE_FIELD);
		dec.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		Object profondeurField = map.get(Ouvrage.PROFONDEUR_FIELD);
		dec.setProfondeur(profondeurField != null ? Double.parseDouble(profondeurField.toString()) : null);
		Object profondeurDeversoirField = map.get(Ouvrage.PROFONDEUR_DEVERSOIR_FIELD);
		dec.setProfondeurDeversoir(
				profondeurDeversoirField != null ? Double.parseDouble(profondeurDeversoirField.toString()) : null);
		Object hauteurDigueField = map.get(Ouvrage.HAUTEUR_DIGUE_FIELD);
		dec.setHauteurDigue(hauteurDigueField != null ? Double.parseDouble(hauteurDigueField.toString()) : null);
		Object typeField = map.get(Ouvrage.TYPE_FIELD);
		if (typeField != null && StringUtils.equalsIgnoreCase("ralentisseur", typeField.toString())) {
			return null;
		}
		Object bvField = map.get(Ouvrage.BV_FIELD);
		dec.setBv(bvField.toString());

		Object zoneField = map.get(Ouvrage.ZONE_FIELD);
		dec.setZone(zoneField.toString());
		return dec;
	}

	private Exutoire recordDBFToExutoire(DbfRecord rec) throws ParseException {
		Exutoire exu = new Exutoire();
		Map<String, Object> map = rec.toMap();
		Object nomField = map.get(Exutoire.NOM_FIELD);
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		exu.setNom(nomField != null ? nomField.toString() : "");
		Object surfaceField = map.get(Exutoire.SURFACE_FIELD);
		exu.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		Object longueurHydroField = map.get(Exutoire.LONGUEUR_FIELD);
		exu.setLongueurHydro(longueurHydroField != null ? Double.parseDouble(longueurHydroField.toString())
				: Exutoire.LONGUEUR_HYDRO_DEFAULT);
		Object deniveleField = map.get(Exutoire.DENIVELE_FIELD);
		exu.setDenivele(
				deniveleField != null ? Double.parseDouble(deniveleField.toString()) : Exutoire.DENIVELE_DEFAULT);
		Object creekField = map.get(Exutoire.CREEK_FIELD);
		exu.setCreek(creekField.toString());

		return exu;
	}

}
