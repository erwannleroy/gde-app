package org.r1.gde.service;

import lombok.Data;

@Data
public class ComputingResult {
	
	boolean inProgress = false;
	boolean computationOk = false;
	byte[] xls;
	int q100InProgress = 0;
	int cassisInProgress = 0;
	int retentionInProgress = 0;
	int objectifsInProgress = 0;
	
	boolean bvDecSent = false;
	boolean decSent = false;
	boolean bvExuSent = false;
	
	boolean objectifsComputing = false;
    boolean objectifsGenere = false;

    boolean retentionComputing = false;
    boolean retentionGenere = false;

    boolean q100Computing = false;
    boolean q100Genere = false;

    boolean cassisComputing = false;
    boolean cassisGenere = false;
	

}
