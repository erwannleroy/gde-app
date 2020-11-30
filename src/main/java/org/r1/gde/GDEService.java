package org.r1.gde;

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
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
				List<Decanteur> decanteurs = parseDEC(resource.getFile());
				result.setFileFormatOk(true);
				result.setNbDecanteurs(decanteurs.size());
				gdeComputer.updateDecanteurs(decanteurs);
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

	private List<Decanteur> parseDEC(File decfile) throws IOException, ParseException {
		List<Decanteur> decanteurs = new ArrayList<Decanteur>();

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
				decanteurs.add(recordDECToDecanteur(rec));
			}
		}
		log.debug("Parcours de " + decanteurs.size() + " décanteurs");
		log.debug("Decanteurs:" + decanteurs);
		return decanteurs;
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
		bv.setDenivele(deniveleField !=null ? Integer.parseInt(deniveleField.toString()) : BassinVersant.DENIVELE_DEFAULT);
		Object longueurField = map.get(BassinVersant.LONGUEUR_FIELD);
		bv.setLongueur(longueurField !=null ? Integer.parseInt(longueurField.toString()) : BassinVersant.LONGUEUR_DEFAULT);
		Object surfaceField = map.get(BassinVersant.SURFACE_FIELD);
		bv.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : BassinVersant.SURFACE_DEFAULT );
		bv.setPerformance(Performance.toPerformance(map.get(BassinVersant.PERFORMANCE_FIELD).toString()));
		return bv;
	}
	
	private Decanteur recordDECToDecanteur(DbfRecord rec) throws ParseException {
		Decanteur dec = new Decanteur();
		Map<String, Object> map = rec.toMap();
		Object nomField = map.get(Decanteur.NOM_FIELD);
		dec.setNom(nomField != null ? nomField.toString() : "");
		Object surfaceField = map.get(Decanteur.SURFACE_FIELD);
		dec.setSurface(surfaceField != null ? Double.parseDouble(surfaceField.toString()) : Decanteur.SURFACE_DEFAULT);
		Object profondeurField = map.get(Decanteur.PROFONDEUR_FIELD);
		dec.setProfondeur(profondeurField !=null ? Double.parseDouble(profondeurField.toString()) : Decanteur.PROFONDEUR_DEFAULT);
		Object profondeurDeversoirField = map.get(Decanteur.PROFONDEUR_DEVERSOIR_FIELD);
		dec.setProfondeurDeversoir(profondeurDeversoirField!=null ? Double.parseDouble(profondeurDeversoirField.toString()) : Decanteur.PROFONDEUR_DEVERSOIR_DEFAULT);
		Object hauteurDigueField = map.get(Decanteur.HAUTEUR_DIGUE_FIELD);
		dec.setHauteurDigue(hauteurDigueField!=null ? Double.parseDouble(hauteurDigueField.toString()) : Decanteur.HAUTEUR_DIGUE_DEFAULT);
		dec.setType(TypeDecanteur.toTypeDecanteur(map.get(Decanteur.TYPE_FIELD).toString()));
		Object bvField = map.get(Decanteur.BV_FIELD);
		dec.setBv(bvField != null ? bvField.toString() : "");
		Object zoneField = map.get(Decanteur.ZONE_FIELD);
		dec.setZone(zoneField != null ? zoneField.toString() : "");
		return dec;
	}

}
