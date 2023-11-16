package org.r1.gde.xls.generator;

public interface SheetGeneratorListener {
	
	void notifyEvent(SheetGeneratorEvent e, Object value) throws GenerateSheetException;

}
