package org.r1.gde.xls.generator;

public class BVNotFoundException extends GDEException {

	String nomBV;

	public BVNotFoundException(String nomBV) {
		super("Le bassin versant " + nomBV + " n'existe pas !");
		this.nomBV = nomBV;
	}

}
