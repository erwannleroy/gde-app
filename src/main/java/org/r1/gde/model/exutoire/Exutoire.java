package org.r1.gde.model.exutoire;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Exutoire {

	public static final String NOM_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String DENIVELE_FIELD = "D_NIVEL_";
	public static final String LONGUEUR_FIELD = "LGR_HYDRO";
	public static final String CREEK_FIELD = "CREEK";
	public static final int LONGUEUR_HYDRO_DEFAULT = 0;
	public static final int DENIVELE_DEFAULT = 0;
	
	
	String nom;
	Double surface;
	Double longueurHydro;
	Double denivele;
	String creek;
}
