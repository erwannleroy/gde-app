package org.r1.gde.xls.generator;

import com.linuxense.javadbf.DBFDataType;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFUtils;
import com.linuxense.javadbf.DBFWriter;
import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfField;
import net.iryndin.jdbf.core.DbfFieldTypeEnum;
import net.iryndin.jdbf.core.DbfMetadata;
import net.iryndin.jdbf.core.DbfRecord;
import net.iryndin.jdbf.reader.DbfReader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BVDecanteurDBFGenerator extends DBFGenerator {

	public void run() {

		log.info("Génération du DBF");

		notifyListeners(DBFGeneratorEvent.DBF_PERF_PROGRESS, (int) 0);

		try {
			appendFile();
		} catch (IOException | ParseException | GDEException e) {
			log.error("Impossible de générer le DBF exutoire", e);
			this.computeContext.getComputingResult().setError(true);
			this.computeContext.getComputingResult().setErrorMsg(e.getMessage());
		}
	}

	private void appendFile() throws IOException, ParseException, GDEException {
		InputStream dbfOrigin = new FileInputStream(this.computeContext.getBvDecFile());

		// Create an temporary file
		Path temp = Files.createTempFile("bv-decanteurs-enrichi", ".dbf");

		DBFWriter writer = new DBFWriter(temp.toFile());

		try (DbfReader reader = new DbfReader(dbfOrigin)) {

			DbfRecord recOrigin;
			DBFField[] fields = createFields(reader.getMetadata());
			writer.setFields(fields);
			int nbBV = reader.getMetadata().getRecordsQty();
			int nbBVTraites = 0;
			while ((recOrigin = reader.read()) != null) {
				writer.addRecord(getRecordTarget(recOrigin, fields));
				nbBVTraites++;
				double progress = (double) 100 / nbBV * nbBVTraites;
				notifyListeners(DBFGeneratorEvent.DBF_PERF_PROGRESS, (int) progress);
			}

			DBFUtils.close(writer);
			this.computeContext.getBytesResult().setBytesPerfDbf(Files.readAllBytes(temp));
			log.info("Ecriture du DBF " + temp.toString());
			notifyListeners(DBFGeneratorEvent.DBF_PERF_GENERATED, null);
		}
	}

	private Object[] getRecordTarget(DbfRecord recOrigin, DBFField[] fields) throws ParseException, GDEException {
		Object[] record = new Object[fields.length];
		Charset stringCharset = Charset.forName("Cp866");
		recOrigin.setStringCharset(stringCharset);
		Map<String, Object> map = recOrigin.toMap();
		int i = 0;

		for (DBFField f : fields) {
			if (!f.getName().equalsIgnoreCase("PERF")) {
				record[i] = map.get(f.getName());
			} else {
				record[i] = computePerfValue(record[0]);
			}
			i++;
		}

		return record;
	}

	private DBFField[] createFields(DbfMetadata meta) {
		List<DBFField> fields = new ArrayList<>();
		boolean perfAlreadyExist = false;
		Collection<DbfField> fieldsOrigin = meta.getFields();
		DBFField fTarget;
		for (DbfField f : fieldsOrigin) {
			fTarget = new DBFField();
			fTarget.setName(f.getName());
			fTarget.setType(getTargetType(f.getType()));
			fTarget.setLength(f.getLength());
			if (f.getType().equals(DbfFieldTypeEnum.Numeric)) {
				fTarget.setDecimalCount(f.getNumberOfDecimalPlaces());
			}
			if (f.getName().contentEquals("PERF")) {
				perfAlreadyExist = true;
			}
			fields.add(fTarget);
		}
		if (!perfAlreadyExist) {
			DBFField perfField = new DBFField();
			perfField.setName("PERF");
			perfField.setType(DBFDataType.CHARACTER);
			perfField.setLength(10);
			fields.add(perfField);
		}
		return fields.toArray(new DBFField[fieldsOrigin.size() + 1]);
	}

	private String computePerfValue(Object nomBV) throws GDEException {
		Double perf = this.computeContext.getPerformanceBVDecanteur().get(nomBV);
		if (perf == null) {
			log.warn("Aucun BV décanteur '" + nomBV + "' n'existe");
			return "";
		}

		if (perf > 100) {
			return "bonne";
		} else if (perf > 80) {
			return "moyenne";
		} else {
			return "mauvaise";
		}
	}
}
