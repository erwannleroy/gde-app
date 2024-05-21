package org.r1.gde.xls.generator;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r1.gde.model.BVDecanteur;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;
import org.r1.gde.service.ComputeContext;

import com.linuxense.javadbf.DBFDataType;

import lombok.extern.slf4j.Slf4j;
import net.iryndin.jdbf.core.DbfFieldTypeEnum;

@Slf4j
public abstract class DBFGenerator implements Runnable {

	public ComputeContext computeContext;
	public List<DBFGeneratorListener> listeners;

		public void addListener(DBFGeneratorListener l) {
		if (listeners == null) {
			listeners = new ArrayList<DBFGeneratorListener>();
		}
		listeners.add(l);
	}

	public void notifyListeners(DBFGeneratorEvent e, Object value) {
		log.info("Event Generator " + e.name() + " - " + value);
		for (DBFGeneratorListener l : listeners) {
			l.notifyEvent(e, value);
		}
	}

	public void generateDBF(ComputeContext computeContext) {
		this.computeContext = computeContext;
		try {
			Thread t = new Thread(this);
			t.start();
		} catch (Exception e) {
			log.error("Impossible de générer", e);
		}
	}
	
	protected DBFDataType getTargetType(DbfFieldTypeEnum type) {
        switch(type) {
            case Character:
                return DBFDataType.CHARACTER;
            case Date:
                return DBFDataType.DATE;
            case Float:
                return DBFDataType.FLOATING_POINT;
            case Memo:
                return DBFDataType.MEMO;
            case Double7:
                return DBFDataType.DOUBLE;
            case Currency:
                return DBFDataType.CURRENCY;
            case General:
                return DBFDataType.GENERAL_OLE;
            case Integer:
                return DBFDataType.NUMERIC;
            case Logical:
                return DBFDataType.LOGICAL;
            case NullFlags:
                return DBFDataType.NULL_FLAGS;
            case Numeric:
                return DBFDataType.NUMERIC;
            case Picture:
                return DBFDataType.PICTURE;
            case Timestamp:
                return DBFDataType.TIMESTAMP;
            case Double:
                return DBFDataType.DOUBLE;
            default:
                throw new RuntimeException("Type non généré : " + type);
        }
    }



}
