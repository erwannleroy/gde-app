package org.r1.gde.service;

import lombok.Data;

@Data
public class ComputingResult {
	
	boolean inProgress = true;
	boolean computationOk = false;
	byte[] xls;

}
