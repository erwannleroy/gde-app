package org.r1.gde.xls.generator;

public class GDEException extends Exception {

	String nomBV;

	public GDEException(String nomBV) {
		super("Le bassin versant " + nomBV + " n'existe pas !");
		this.nomBV = nomBV;
	}

}
