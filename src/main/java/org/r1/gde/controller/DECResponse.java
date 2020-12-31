package org.r1.gde.controller;

import lombok.Data;

@Data
public class DECResponse {

	private boolean fileExists = false;
	private boolean fileFormatOk = false;
	private String errorMessage = "";
	private int nbZone = 0;
	private boolean error = false;
}
