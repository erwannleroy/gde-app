package org.r1.gde.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Exutoire;
import org.r1.gde.model.Zone;

public class TempExutoiresParsing {

	List<Exutoire> exutoires = new ArrayList<>();

	public List<Exutoire> getExutoires() {
		return exutoires;
	}

	public void addExutoire(Exutoire exutoire) {
		exutoires.add(exutoire);
	}

	
}
