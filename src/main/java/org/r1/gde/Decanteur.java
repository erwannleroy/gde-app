package org.r1.gde;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Decanteur {

	public static final String NOM_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String PROFONDEUR_FIELD = "PROFONDEUR";
	public static final String PROFONDEUR_DEVERSOIR_FIELD = "D_VERSOIR";
	public static final String HAUTEUR_DIGUE_FIELD = "DIGUE";
	public static final String BV_FIELD = "BV";
	public static final String TYPE_FIELD = "TYPE";
	public static final String ZONE_FIELD = "ZONE";
	public static final int PROFONDEUR_DEFAULT = -1;
	public static final int PROFONDEUR_DEVERSOIR_DEFAULT = 0;
	public static final int HAUTEUR_DIGUE_DEFAULT = 0;
	public static final double SURFACE_DEFAULT = -1;
	
	
	String nom;
	Double surface;
	Double profondeur;
	Double profondeurDeversoir;
	Double hauteurDigue;
	String bv;
	String zone;
	TypeDecanteur type;
}
