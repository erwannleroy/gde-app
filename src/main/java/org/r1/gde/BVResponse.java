package org.r1.gde;

import lombok.Data;

@Data
public class BVResponse {

	private boolean fileExists = false;
	private boolean fileFormatOk = false;
	private String errorMessage = "";
	private int nbBassins = 0;
	private boolean error = false;
}
