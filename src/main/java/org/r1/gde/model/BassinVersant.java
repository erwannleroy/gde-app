package org.r1.gde.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BassinVersant {

	public static final String NOM_OUVRAGE_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String DENIVELE_FIELD = "D_NIVEL_";
	public static final String LONGUEUR_FIELD = "LGR_HYDRO";
	public static final String TYPE_FIELD = "TYPE";


	public String nomOuvrage;
	public Double surface;
	public Integer denivele;
	public Integer longueur;
}
