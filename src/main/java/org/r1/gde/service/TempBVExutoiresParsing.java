package org.r1.gde.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.r1.gde.model.BVExutoire;
import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Creek;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;

public class TempBVExutoiresParsing {

	List<Creek> creeks = new ArrayList<Creek>();

	public List<Creek> getCreeks() {
		return creeks;
	}

	public void addBVExutoire(BVExutoire bvExutoire) {
		Creek c = getCreek(bvExutoire.getCreek());
		c.exutoires.add(bvExutoire);
	}

	private Creek getCreek(String nomCreek) {
		Creek creek = getOrCreateCreek(nomCreek);
		return creek;
	}

	private Creek getOrCreateCreek(String nomCreek) {
		boolean trouve = false;
		Creek creek = null;
		Iterator<Creek> it = creeks.iterator();
		while (!trouve && it.hasNext()) {
			Creek currentCreek = it.next();
			if (currentCreek.getNom().equalsIgnoreCase(nomCreek)) {
				trouve = true;
				creek = currentCreek;
			}
		}

		if (!trouve) {
			creek = new Creek();
			creek.setNom(nomCreek);
			creeks.add(creek);
		}
		return creek;
	}
	
}
