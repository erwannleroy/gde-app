package org.r1.gde.service;

import lombok.Data;

@Data
public class ComputingResult {

	boolean error = false;
	String errorMsg = "";

	boolean bytesXlsInProgress = false;
	boolean bytesDbfInProgress = false;
	boolean xlsComputationOk = false;
	boolean dbfComputationOk = false;

	boolean decSent = false;
	boolean bvDecSent = false;
	boolean bvExuSent = false;

	int objRetComputeProgress = 0;
	int retComputeProgress = 0;
	int cassisComputeProgress = 0;
	int q100ComputeProgress = 0;

	boolean objRetComputeOk = false;
	boolean retComputeOk = false;
	boolean cassisComputeOk = false;
	boolean q100ComputeOk = false;

	int xlsComputeProgress = 0;
	int dbfComputeProgress = 0;
}
