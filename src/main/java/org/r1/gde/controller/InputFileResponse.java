package org.r1.gde.controller;

import lombok.Data;

@Data
public abstract class InputFileResponse {
	private boolean fileExists = false;
	private boolean fileFormatOk = false;
	private String errorMessage = "";
	private boolean error = false;
	
}
