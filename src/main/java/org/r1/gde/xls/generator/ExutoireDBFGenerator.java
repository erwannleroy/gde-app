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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ExutoireDBFGenerator extends DBFGenerator {

	public void run() {

		log.info("Génération du DBF exutoire");

		notifyListeners(DBFGeneratorEvent.DBF_DEBIT_PROGRESS, (int) 0);

		try {
			appendFile();
		} catch (IOException | ParseException | GDEException e) {
			log.error("Impossible de générer le DBF exutoire", e);
			this.computeContext.getComputingResult().setError(true);
			this.computeContext.getComputingResult().setErrorMsg(e.getMessage());
		}
	}

	private void appendFile() throws IOException, ParseException, GDEException {
		InputStream dbfOrigin = new FileInputStream(this.computeContext.getExuFile());

		// Create an temporary file
		Path temp = Files.createTempFile("bv-decanteurs-enrichi", ".dbf");

		DBFWriter writer = new DBFWriter(temp.toFile());

		try (DbfReader reader = new DbfReader(dbfOrigin)) {

			DbfRecord recOrigin;
			DBFField[] fields = createFields(reader.getMetadata());
			writer.setFields(fields);
			int nbExu = reader.getMetadata().getRecordsQty();
			int nbExuTraites = 0;
			while ((recOrigin = reader.read()) != null) {
				writer.addRecord(getRecordTarget(recOrigin, fields));
				nbExuTraites++;
				double progress = (double) 100 / nbExu * nbExuTraites;
				notifyListeners(DBFGeneratorEvent.DBF_DEBIT_PROGRESS, (int) progress);
			}

			DBFUtils.close(writer);
			this.computeContext.getBytesResult().setBytesDebitDbf(Files.readAllBytes(temp));
			log.info("Ecriture du DBF debit" + temp.toString());
			notifyListeners(DBFGeneratorEvent.DBF_DEBIT_GENERATED, null);
		}
	}

	private Object[] getRecordTarget(DbfRecord recOrigin, DBFField[] fields) throws ParseException, GDEException {
		Object[] record = new Object[fields.length];
		Charset stringCharset = Charset.forName("Cp866");
		recOrigin.setStringCharset(stringCharset);
		Map<String, Object> map = recOrigin.toMap();
		int i = 0;

		for (DBFField f : fields) {
			if (!f.getName().equalsIgnoreCase("Q100")) {
				record[i] = map.get(f.getName());
			} else {
				Double debitBv = this.computeContext.getDebitBVExutoire().get(record[0]);
				if (debitBv == null) {
					log.warn("Aucun BV exutoire '" + record[0] + "' n'existe");
				} else {
					record[i] = computeDebitValue(debitBv);
				}
			}
			i++;
		}

		return record;
	}

	private DBFField[] createFields(DbfMetadata meta) {
		List<DBFField> fields = new ArrayList<>();
		boolean debitAlreadyExist = false;
		Collection<DbfField> fieldsOrigin = meta.getFields();
		DBFField fTarget;
		int nbFields = fieldsOrigin.size();
		for (DbfField f : fieldsOrigin) {
			fTarget = new DBFField();
			fTarget.setName(f.getName());
			fTarget.setType(getTargetType(f.getType()));
			fTarget.setLength(f.getLength());
			if (f.getType().equals(DbfFieldTypeEnum.Numeric)) {
				fTarget.setDecimalCount(f.getNumberOfDecimalPlaces());
			}
			if (f.getName().contentEquals("Q100")) {
				debitAlreadyExist = true;
			}
			fields.add(fTarget);
		}
		if (!debitAlreadyExist) {
			DBFField perfField = new DBFField();
			perfField.setName("Q100");
			perfField.setType(DBFDataType.CHARACTER);
			perfField.setLength(30);
			fields.add(perfField);
			nbFields++;
		}
		return fields.toArray(new DBFField[nbFields]);
	}

	private String computeDebitValue(Double debit) {
		if (debit <= 0.1) {
			return "Q100 <= 0.1 m3/s";
		} else if (debit > 0.1 && debit <= 0.5) {
			return "0.1 < Q100 <= 0.5 m3/s";
		} else if (debit > 0.5 && debit <= 1) {
			return "0.5 < Q100 <= 1 m3/s";
		} else if (debit > 1 && debit <= 2) {
			return "1 < Q100 <= 2 m3/s";
		} else if (debit > 2 && debit <= 5) {
			return "2 < Q100 <= 5 m3/s";
		} else {
			return new DecimalFormat("0.00").format(debit);
		}
	}

}
