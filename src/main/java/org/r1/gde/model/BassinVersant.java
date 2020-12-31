package org.r1.gde.model;

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


	public String nomOuvrage;
	public Double surface;
	public Integer denivele;
	public Integer longueur;
	public Performance performance;
}
