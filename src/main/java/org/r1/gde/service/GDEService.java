package org.r1.gde.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.r1.gde.controller.BVDecanteurResponse;
import org.r1.gde.controller.DecanteurResponse;
import org.r1.gde.controller.ExutoireResponse;
import org.r1.gde.controller.BVExutoireResponse;
import org.r1.gde.demo.FileStorageService;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.DonneesMeteo;
import org.r1.gde.model.Exutoire;
import org.r1.gde.model.Zone;
import org.r1.gde.xls.generator.GDEException;
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

	public BVDecanteurResponse giveBVDecanteurFile(MultipartFile file) {

		clearError();
		gdeComputer.getComputeContext().getComputingResult().setObjRetComputeOk(false);

		log.debug("giveBVDecanteurFile");

		String storeFile = this.fileStorageService.storeFile(file);

		BVDecanteurResponse result = new BVDecanteurResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<BVDecanteur> bassins = parseBVDecanteur(resource.getFile());
				result.setFileFormatOk(true);
				result.setNbBassins(bassins.size());
				gdeComputer.updateBassins(bassins);
			} catch (IOException e) {
				log.error("Impossible de parser le fichier BV", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult()
						.setErrorMsg("Le fichier des BV décanteurs est mal structuré (cause : " + e.getMessage() + ")");
			} catch (GDEException e) {
				log.error("Impossible de parser le fichier BV", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult().setErrorMsg(e.getMessage());
			}
		}
		return result;
	}

	private void clearError() {
		gdeComputer.getComputeContext().getComputingResult().setError(false);
		gdeComputer.getComputeContext().getComputingResult().setErrorMsg("");
	}

	public DecanteurResponse giveDecanteurFile(MultipartFile file) {
		clearError();
		gdeComputer.getComputeContext().getComputingResult().setRetComputeOk(false);
		log.debug("giveDecanteurFile");

		String storeFile = this.fileStorageService.storeFile(file);

		DecanteurResponse result = new DecanteurResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<Zone> zones = parseDecanteur(resource.getFile());
				result.setFileFormatOk(true);
				fillResult(result, zones);
				gdeComputer.updateDecanteurs(zones);
			} catch (IOException e) {
				log.error("Impossible de parser le fichier DEC", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult()
						.setErrorMsg("Le fichier des décanteurs est mal structuré (cause : " + e.getMessage() + ")");
			} catch (GDEException e) {
				log.error("Impossible de parser le fichier DEC", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult().setErrorMsg(e.getMessage());
			}
		}
		return result;
	}

	private void fillResult(DecanteurResponse result, List<Zone> zones) {
		result.setNbZones(zones.size());
		int nbBVs = 0;
		int nbOuvrages = 0;

		for (Zone z : zones) {
			nbBVs += z.getBassins().size();
			for (org.r1.gde.model.BassinVersant b : z.getBassins()) {
				nbOuvrages += b.getOuvrages().size();
			}
		}

		result.setNbBVs(nbBVs);
		result.setNbDecanteurs(nbOuvrages);
	}

	public BVExutoireResponse giveBVExutoireFile(MultipartFile file) {
		clearError();
		gdeComputer.getComputeContext().getComputingResult().setQ100ComputeOk(false);
		gdeComputer.getComputeContext().getComputingResult().setCassisComputeOk(false);
		log.debug("giveBVExutoireFile");

		String storeFile = this.fileStorageService.storeFile(file);

		BVExutoireResponse result = new BVExutoireResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<Creek> creeks = parseBVExutoire(resource.getFile());
				result.setFileFormatOk(true);
				fillResult(result, creeks);
				gdeComputer.updateBVExutoires(creeks);
			} catch (IOException e) {
				log.error("Impossible de parser le fichier BV EXU", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult()
						.setErrorMsg("Le fichier des BV exutoires est mal structuré (cause : " + e.getMessage() + ")");
			} catch (GDEException e) {
				log.error("Impossible de parser le fichier BV EXU", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult().setErrorMsg(e.getMessage());
			}
		}
		return result;
	}

	public ExutoireResponse giveExutoireFile(MultipartFile file) {
		clearError();
		gdeComputer.getComputeContext().getComputingResult().setDebitDbfComputationOk(false);
		log.debug("giveExutoireFile");

		String storeFile = this.fileStorageService.storeFile(file);

		ExutoireResponse result = new ExutoireResponse();
		Resource resource = this.fileStorageService.loadFileAsResource(storeFile);

		if (resource.exists()) {
			result.setFileExists(true);
			try {
				List<Exutoire> exutoires = parseExutoire(resource.getFile());
				result.setFileFormatOk(true);
				result.setNbExutoires(exutoires.size());
				gdeComputer.updateExutoires(exutoires);
			} catch (IOException e) {
				log.error("Impossible de parser le fichier EXU", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult()
						.setErrorMsg("Le fichier des exutoires est mal structuré (cause : " + e.getMessage() + ")");
			} catch (GDEException e) {
				log.error("Impossible de parser le fichier EXU", e);
				result.setFileFormatOk(false);
				result.setErrorMessage(e.getMessage());
				result.setError(true);
				gdeComputer.getComputeContext().getComputingResult().setError(true);
				gdeComputer.getComputeContext().getComputingResult().setErrorMsg(e.getMessage());
			}
		}
		return result;
	}

	private void fillResult(BVExutoireResponse result, List<Creek> creeks) {

		result.setNbCreeks(creeks.size());
		int nbExutoires = 0;

		for (Creek c : creeks) {
			nbExutoires += c.getExutoires().size();
		}
		result.setNbExutoires(nbExutoires);
	}

	private List<Zone> parseDecanteur(File decfile) throws GDEException {
		TempOuvragesParsing tempResult = new TempOuvragesParsing();

		log.debug("Parsing du bv " + decfile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		InputStream dbf;
		try {
			dbf = new FileInputStream(decfile);
		} catch (FileNotFoundException e) {
			throw new GDEException("Le fichier des décanteurs est mal structuré", e);
		}

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();
			checkColumns(meta, Decanteur.fields());
			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				Decanteur decanteur = recordDBFToDecanteur(rec);
				if (decanteur != null) {
					tempResult.addOuvrage(decanteur);
				}
			}
		} catch (IOException e) {
			throw new GDEException("Le fichier des décanteurs est mal structuré", e);
		} catch (ParseException e) {
			throw new GDEException("Le fichier des décanteurs est mal structuré", e);
		} finally {
			try {
				dbf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Parcours de " + tempResult.zones.size() + " zones");
//		log.debug("Ouvrage:" + ouvrages);
		return tempResult.getZones();
	}

	private List<Creek> parseBVExutoire(File exufile) throws GDEException {
		List<Creek> creekszones = new ArrayList<Creek>();
		TempBVExutoiresParsing tempResult = new TempBVExutoiresParsing();

		log.debug("Parsing du exuFile " + exufile.getAbsolutePath());
		Charset stringCharset = Charset.forName("ISO-8859-1");

		InputStream dbf;
		try {
			dbf = new FileInputStream(exufile);
		} catch (FileNotFoundException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		}

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			checkColumns(meta, BVExutoire.fields());
			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				BVExutoire bvExu = recordDBFToBVExutoire(rec);
				if (bvExu != null) {
					tempResult.addBVExutoire(bvExu);
				}
			}
		} catch (IOException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		} finally {
			try {
				dbf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Parcours de " + tempResult.creeks.size() + " creeks");
		return tempResult.getCreeks();
		
	}

	private void checkColumns(DbfMetadata meta, List<String> fields) throws GDEException {
		for (String f : fields) {
			if (meta.getField(f) == null) {
				throw new GDEException("Le fichier DBF doit définir une colonne '" + f + "'.");
			}
		}
	}

	private List<Exutoire> parseExutoire(File exufile) throws GDEException {
		TempExutoiresParsing tempResult = new TempExutoiresParsing();

		log.debug("Parsing du exuFile " + exufile.getAbsolutePath());
		Charset stringCharset = Charset.forName("ISO-8859-1");

		this.gdeComputer.getComputeContext().setExuFile(exufile);

		InputStream dbf;
		try {
			dbf = new FileInputStream(exufile);
		} catch (FileNotFoundException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		}

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();
			checkColumns(meta, Exutoire.fields());
			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				Exutoire exu = recordDBFToExutoire(rec);
				if (exu != null) {
					tempResult.addExutoire(exu);
				}
			}
		} catch (IOException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		} finally {
			try {
				dbf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Parcours de " + tempResult.exutoires.size() + " exutoires");
		return tempResult.getExutoires();
	}

	private List<BVDecanteur> parseBVDecanteur(File bvFile) throws GDEException {
		List<BVDecanteur> bassins = new ArrayList<BVDecanteur>();

		log.debug("Parsing du bv " + bvFile.getAbsolutePath());
		Charset stringCharset = Charset.forName("Cp866");

		this.gdeComputer.getComputeContext().setBvDecFile(bvFile);
		InputStream dbf;
		try {
			dbf = new FileInputStream(bvFile);
		} catch (FileNotFoundException e) {
			throw new GDEException("Le fichier des BV décanteurs est mal structuré", e);
		}

		DbfRecord rec;
		try (DbfReader reader = new DbfReader(dbf)) {
			DbfMetadata meta = reader.getMetadata();

			checkColumns(meta, BVDecanteur.fields());
			log.debug("Read DBF Metadata: " + meta);
			while ((rec = reader.read()) != null) {
				rec.setStringCharset(stringCharset);
				log.debug("Record #" + rec.getRecordNumber() + ": " + rec.toMap());
				BVDecanteur bvDec = recordDBFToBVDecanteur(rec);
				if (bvDec != null) {
					bassins.add(bvDec);
				}
			}
		} catch (IOException e) {
			throw new GDEException("Le fichier des BV décanteurs est mal structuré", e);
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV décanteurs est mal structuré", e);
		} finally {
			try {
				dbf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.debug("Parcours de " + bassins.size() + " bassins versants");
		log.debug("Bassins:" + bassins);
		return bassins;
	}

	private BVDecanteur recordDBFToBVDecanteur(DbfRecord rec) throws GDEException {
		BVDecanteur bv = new BVDecanteur();
		Map<String, Object> map;
		try {
			map = rec.toMap();
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV décanteurs est mal structuré", e);
		}
		Object nomField = map.get(BVDecanteur.NOM_OUVRAGE_FIELD);
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		bv.setNomOuvrage(nomField != null ? nomField.toString() : "");
		Object deniveleField = map.get(BVDecanteur.DENIVELE_FIELD);
		bv.setDenivele(deniveleField != null ? Integer.parseInt(deniveleField.toString()) : null);
		Object longueurField = map.get(BVDecanteur.LONGUEUR_FIELD);
		bv.setLongueur(longueurField != null ? Integer.parseInt(longueurField.toString()) : null);
		Object surfaceField = map.get(BVDecanteur.SURFACE_FIELD);
		bv.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		return bv;
	}

	private Decanteur recordDBFToDecanteur(DbfRecord rec) throws GDEException {
		Decanteur dec = new Decanteur();
		Map<String, Object> map;
		try {
			map = rec.toMap();
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV décanteurs est mal structuré", e);
		}
		Object nomField = map.get(Decanteur.NOM_FIELD);
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		dec.setNom(nomField != null ? nomField.toString() : "");
		Object surfaceField = map.get(Decanteur.SURFACE_FIELD);
		dec.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		Object profondeurField = map.get(Decanteur.PROFONDEUR_FIELD);
		dec.setProfondeur(profondeurField != null ? Double.parseDouble(profondeurField.toString()) : null);
		Object profondeurDeversoirField = map.get(Decanteur.PROFONDEUR_DEVERSOIR_FIELD);
		dec.setProfondeurDeversoir(
				profondeurDeversoirField != null ? Double.parseDouble(profondeurDeversoirField.toString()) : null);
		Object hauteurDigueField = map.get(Decanteur.HAUTEUR_DIGUE_FIELD);
		dec.setHauteurDigue(hauteurDigueField != null ? Double.parseDouble(hauteurDigueField.toString()) : null);
		Object typeField = map.get(Decanteur.TYPE_FIELD);
		if (typeField != null && !StringUtils.equalsIgnoreCase("FF", typeField.toString())
				&& !StringUtils.equalsIgnoreCase("fond de fosse", typeField.toString())
				&& !StringUtils.equalsIgnoreCase("dщcanteur", typeField.toString())
				&& !StringUtils.equalsIgnoreCase("decanteur", typeField.toString())
				&& !StringUtils.equalsIgnoreCase("décanteur", typeField.toString())) {
			return null;

		}
		Object bvField = map.get(Decanteur.BV_FIELD);
		dec.setBv(bvField.toString());

		Object zoneField = map.get(Decanteur.ZONE_FIELD);
		dec.setZone(zoneField.toString());
		return dec;
	}

	private BVExutoire recordDBFToBVExutoire(DbfRecord rec) throws GDEException {
		BVExutoire exu = new BVExutoire();
		Map<String, Object> map;
		try {
			map = rec.toMap();
		} catch (ParseException e) {
			throw new GDEException("Le fichier des BV exutoires est mal structuré", e);
		}

		Object nomField = map.get(BVExutoire.NOM_FIELD);
		Object surfaceField = map.get(BVExutoire.SURFACE_FIELD);
		Object longueurHydroField = map.get(BVExutoire.LONGUEUR_FIELD);
		Object deniveleField = map.get(BVExutoire.DENIVELE_FIELD);
		Object creekField = map.get(BVExutoire.CREEK_FIELD);

		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		exu.setNom(nomField != null ? nomField.toString() : "");
		exu.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : null);
		exu.setLongueurHydro(longueurHydroField != null ? Double.parseDouble(longueurHydroField.toString())
				: BVExutoire.LONGUEUR_HYDRO_DEFAULT);
		exu.setDenivele(
				deniveleField != null ? Double.parseDouble(deniveleField.toString()) : BVExutoire.DENIVELE_DEFAULT);
		if (creekField == null || StringUtils.isBlank(creekField.toString())) {
			throw new GDEException("Le creek du BV exutoire '" + exu.getNom() + "' doit être défini");
		}
		exu.setCreek(creekField.toString());

		return exu;
	}

	private Exutoire recordDBFToExutoire(DbfRecord rec) throws GDEException {
		Exutoire exu = new Exutoire();
		Map<String, Object> map;
		try {
			map = rec.toMap();
		} catch (ParseException e) {
			throw new GDEException("Le fichier des exutoires est mal structuré", e);
		}
		Object nomField = map.get(Exutoire.NOM_FIELD);
		if (nomField == null || StringUtils.isEmpty(nomField.toString())) {
			return null;
		}
		exu.setNom(nomField != null ? nomField.toString() : "");

		return exu;
	}

	public MeteoResponse applyMeteo(DonneesMeteo data) {
		this.gdeComputer.updateMeteo(data);
		log.info("Définition des parametres météo : " + data.toString());
		MeteoResponse mr = new MeteoResponse();
		mr.result = true;
		return mr;
	}

}
