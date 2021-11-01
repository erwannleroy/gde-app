package org.r1.gde.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.r1.gde.model.BassinVersant;
import org.r1.gde.model.Decanteur;
import org.r1.gde.model.Zone;

public class TempOuvragesParsing {

	List<Zone> zones = new ArrayList<Zone>();

	public List<Zone> getZones() {
		return zones;
	}

	public void addOuvrage(Decanteur ouv) {
		BassinVersant bv = getBassinVersant(ouv.getZone(), ouv.getBv());
		bv.ouvrages.add(ouv);
	}

	private BassinVersant getBassinVersant(String nomZone, String nomBv) {
		Zone zone = getOrCreateZone(nomZone);
		return getOrCreateBassinVersant(zone, nomBv);
	}

	private Zone getOrCreateZone(String nomZone) {
		boolean trouve = false;
		Zone zone = null;
		Iterator<Zone> it = zones.iterator();
		while (!trouve && it.hasNext()) {
			Zone currentZone = it.next();
			if (currentZone.getNom().equalsIgnoreCase(nomZone)) {
				trouve = true;
				zone = currentZone;
			}
		}

		if (!trouve) {
			zone = new Zone();
			zone.setNom(nomZone);
			zones.add(zone);
		}
		return zone;
	}
	
	private BassinVersant getOrCreateBassinVersant(Zone zone, String nomBv) {
		boolean trouve = false;
		BassinVersant bv = null;
		Iterator<BassinVersant> it = zone.getBassins().iterator();
		while (!trouve && it.hasNext()) {
			BassinVersant currentBv = it.next();
			if (currentBv.getNom().equalsIgnoreCase(nomBv)) {
				trouve = true;
				bv = currentBv;
			}
		}

		if (!trouve) {
			bv = new BassinVersant();
			bv.setNom(nomBv);
			zone.bassins.add(bv);
		}
		return bv;
	}

}
