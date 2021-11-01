package org.r1.gde.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"ouvrages"})
public class BassinVersant {

	public String nom;
	public List<Decanteur> ouvrages = new ArrayList<Decanteur>();
}
