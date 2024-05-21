package org.r1.gde.model;


import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BVExutoire {

	public static final String NOM_FIELD = "NOM";
	public static final String SURFACE_FIELD = "SURFACE";
	public static final String DENIVELE_FIELD = "DENIVELE";
	public static final String LONGUEUR_FIELD = "LGR_HYDRO";
	public static final String CREEK_FIELD = "CREEK";
	public static final int LONGUEUR_HYDRO_DEFAULT = 0;
	public static final int DENIVELE_DEFAULT = 0;
	
	
	String nom;
	Double surface;
	Double longueurHydro;
	Double denivele;
	String creek;

	@Override
	public BVExutoire clone() {
		BVExutoire bv = new BVExutoire();
		bv.setNom(this.nom);
		bv.setSurface(this.surface);
		bv.setLongueurHydro(this.longueurHydro);
		bv.setDenivele(this.denivele);
		bv.setCreek(this.creek);
		return bv;
	}

	public static List<String> fields() {
		return Arrays.asList(NOM_FIELD, SURFACE_FIELD, DENIVELE_FIELD, LONGUEUR_FIELD, CREEK_FIELD);
	}
}
