package org.r1.gde.model;

public enum Performance {

	MAUVAISE("mauvaise"), MOYENNE("moyenne"), BONNE("bonne");

	public String libelle;

	Performance(String libelle) {
		this.libelle = libelle;
	}

	public static Performance toPerformance(String libelle) {
		switch (libelle) {

		case "moyenne" :
			return Performance.MOYENNE;
		case "bonne" :
			return Performance.BONNE;
		case "mauvaise":
			return Performance.MAUVAISE;
		default:
			throw new RuntimeException("cette valeur de performance n'est pas valide : " + libelle);
		}

	}
}
