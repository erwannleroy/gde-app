package org.r1.gde;

import lombok.Data;

@Data
public class DECResponse {

	private boolean fileExists = false;
	private boolean fileFormatOk = false;
	private String errorMessage = "";
	private int nbDecanteurs= 0;
	private boolean error = false;
}
