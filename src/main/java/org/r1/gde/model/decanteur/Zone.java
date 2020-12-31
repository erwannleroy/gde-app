package org.r1.gde.model.decanteur;

import java.util.ArrayList;
import java.util.List;

import org.r1.gde.model.TypeDecanteur;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Zone {

	
	public String nom;
	public List<BassinVersant> bassins = new ArrayList<>();
}
