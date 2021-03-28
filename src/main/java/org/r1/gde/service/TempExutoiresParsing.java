package org.r1.gde.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.r1.gde.model.decanteur.BassinVersant;
import org.r1.gde.model.decanteur.Ouvrage;
import org.r1.gde.model.decanteur.Zone;
import org.r1.gde.model.exutoire.Creek;
import org.r1.gde.model.exutoire.Exutoire;

public class TempExutoiresParsing {

	List<Creek> creeks = new ArrayList<Creek>();

	public List<Creek> getCreeks() {
		return creeks;
	}

	public void addExutoire(Exutoire exutoire) {
		Creek c = getCreek(exutoire.getCreek());
		c.exutoires.add(exutoire);
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
