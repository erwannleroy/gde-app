package org.r1.gde.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Creek {

	
	public String nom;
	public List<BVExutoire> exutoires = new ArrayList<>();
	
	@Override
	public Creek clone() {
		Creek c = new Creek();
		c.nom = this.nom;
		for (BVExutoire bv : exutoires) {
			c.exutoires.add(bv.clone());
		}
		return c;
	}
	
	
}
