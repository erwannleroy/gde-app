package org.r1.gde.xls.generator;

public class GDEException extends Exception {

	public GDEException(String message, Throwable e) {
		super(message + " (cause : " + e.getMessage() + ")");
	}

	public GDEException(String message) {
		super(message);
	}

}
