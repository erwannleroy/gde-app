package org.r1.gde.model;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BVDecanteur {

	public static final String NOM_OUVRAGE_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String DENIVELE_FIELD = "DENIVELE";
	public static final String LONGUEUR_FIELD = "LGR_HYDRO";

	public String nomOuvrage;
	public Double surface;
	public Integer denivele;
	public Integer longueur;
	
	public static List<String> fields() {
		return Arrays.asList(NOM_OUVRAGE_FIELD, SURFACE_FIELD, DENIVELE_FIELD, LONGUEUR_FIELD);
	}
}
