package org.r1.gde.model;


import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Exutoire {

	public static final String NOM_FIELD = "NOM";
	public static final String Q100_FIELD = "Q100";
	
	String nom;
	String debit;

	@Override
	public Exutoire clone() {
		Exutoire e = new Exutoire();
		e.setNom(this.nom);
		e.setDebit(this.debit);
		return e;
	}

	public static List<String> fields() {
		return Arrays.asList(NOM_FIELD);
	}
}
