package org.r1.gde;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Decanteur {

	public static final String NOM_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String PROFONDEUR_FIELD = "PROFONDEUR";
	public static final String TYPE_FIELD = "TYPE";
	public static final String EQUIPEMENT_FIELD = "AQUIPEMENT";
	public static final String BV_FIELD = "BV";
	public static final int PROFONDEUR_DEFAULT = -1;
	public static final double SURFACE_DEFAULT = -1;

	String nom;
	Double surface;
	Double profondeur;
	String equipement;
	String bv;
	TypeDecanteur type;
}
