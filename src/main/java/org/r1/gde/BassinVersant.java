package org.r1.gde;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BassinVersant {

	public static final String NOM_OUVRAGE_FIELD = "NOM_OUVRAG";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String DENIVELE_FIELD = "D_NIVEL_";
	public static final String LONGUEUR_FIELD = "LONGUEUR_H";
	public static final String PERFORMANCE_FIELD = "PERFORMANC";
	public static final int DENIVELE_DEFAULT = -1;
	public static final int LONGUEUR_DEFAULT = -1;
	public static final double SURFACE_DEFAULT = -1;

	String nomOuvrage;
	Double surface;
	Integer denivele;
	Integer longueur;
	Performance performance;
}
