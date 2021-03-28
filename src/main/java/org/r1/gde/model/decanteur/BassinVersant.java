package org.r1.gde.model.decanteur;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BassinVersant {

	public String nom;
	public List<Ouvrage> ouvrages = new ArrayList<Ouvrage>();
}
