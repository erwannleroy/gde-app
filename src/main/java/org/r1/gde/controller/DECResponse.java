package org.r1.gde.controller;

import lombok.Data;

@Data
public class DECResponse extends InputFileResponse {

	private int nbZones = 0;
	private int nbBVs = 0;
	private int nbDecanteurs = 0;
}
