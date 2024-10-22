package org.r1.gde.xls.generator;

public class ExutoireNotFoundException extends GDEException {

	String nomBV;

	public ExutoireNotFoundException(String nomBV) {
		super("Le bassin versant " + nomBV + " n'existe pas !");
		this.nomBV = nomBV;
	}

}
