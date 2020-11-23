package org.r1.gde;

public enum TypeDecanteur {

	RALENTISSEUR("ralentisseur"), DECANTEUR("décanteur"), RETENUE("retenue"),
	FOND_FOSSE("fond de fosse"), STAGNATION("stagnation");

	public String libelle;

	TypeDecanteur(String libelle) {
		this.libelle = libelle;
	}

	static TypeDecanteur toTypeDecanteur(String libelle) {
		switch (libelle) {

		case "ralentisseur":
			return TypeDecanteur.RALENTISSEUR;
		case "décanteur":
		case "d?canteur":
		case "dщcanteur":
			return TypeDecanteur.DECANTEUR;
		case "retenue":
			return TypeDecanteur.RETENUE;
		case "fond de fosse":
			return TypeDecanteur.FOND_FOSSE;
		case "stagnation":
			return TypeDecanteur.STAGNATION;
		default:
			throw new RuntimeException("cette valeur de type de décanteur n'est pas valide : " + libelle);
		}

	}
}
