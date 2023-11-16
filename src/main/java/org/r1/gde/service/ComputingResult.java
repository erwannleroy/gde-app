package org.r1.gde.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ComputingResult {

	boolean error = false;
	String errorMsg = "";

	boolean bytesXlsInProgress = false;
	boolean bytesDbfInProgress = false;
	boolean xlsComputationOk = false;
	boolean perfDbfComputationOk = false;
	boolean debitDbfComputationOk = false;

	boolean decSent = false;
	boolean bvDecSent = false;
	boolean bvExuSent = false;
	boolean exuSent = false;

	int objRetComputeProgress = 0;
	int retComputeProgress = 0;
	int cassisComputeProgress = 0;
	int q100ComputeProgress = 0;

	boolean objRetComputeOk = false;
	boolean retComputeOk = false;
	boolean cassisComputeOk = false;
	boolean q100ComputeOk = false;

	int xlsComputeProgress = 0;
	int perfDbfComputeProgress = 0;
	int debitDbfComputeProgress = 0;

	// en import
	List<String> inDbfBVDecWarns = new ArrayList<String>();
	List<String> inDbfDecWarns = new ArrayList<String>();
	List<String> inDbfBVExuWarns = new ArrayList<String>();
	List<String> inDbfExuWarns = new ArrayList<String>();
	// génération excel
	List<String> paramWarns = new ArrayList<String>();
	List<String> objRetWarns = new ArrayList<String>();
	List<String> retBassinsWarns = new ArrayList<String>();
	List<String> q100Warns = new ArrayList<String>();
	List<String> cassisWarns = new ArrayList<String>();
	// génération DBF
	List<String> bvDecDBFWarns = new ArrayList<String>();
	List<String> exuDBFWarns = new ArrayList<String>();

}
