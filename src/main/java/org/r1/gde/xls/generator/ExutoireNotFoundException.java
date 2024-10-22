package org.r1.gde.xls.generator;

public class ExutoireNotFoundException extends GDEException {

	String nomExu;

	public ExutoireNotFoundException(String nomExu) {
		super("L'exutoire' " + nomExu + " n'existe pas !");
		this.nomExu = nomExu;
	}

}
