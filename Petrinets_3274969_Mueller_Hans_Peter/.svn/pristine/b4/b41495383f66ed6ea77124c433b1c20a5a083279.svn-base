package io.petermueller.petrinetz.models.filesystem;

import java.io.File;
import propra.pnml.PNMLWopedParser;
import io.petermueller.petrinetz.models.petrinet.*;

public class PNMLParser extends PNMLWopedParser {
	private PetriNetModel model;
	
	public PNMLParser(File file, PetriNetModel model) {
		super(file);
		this.model = model;
	}
	
	@Override 
	public void newTransition(final String id) {
		System.out.println("Transition mit id " + id + " wurde gefunden.");
		model.addTransition(id);
	}
	
	@Override 
	public void newPlace(final String id) {
		System.out.println("Stelle mit id " + id + " wurde gefunden.");
		model.addPlace(id);
	}

	@Override 
	public void newArc(final String id, final String source, final String target) {
		System.out.println("Kante mit id " + id + " von " + source + " nach "
				+ target + " wurde gefunden.");
		model.addArc(id, source, target);
	}

	@Override 
	public void setPosition(final String id, final String x, final String y) {
		System.out.println("Setze die Position des Elements " + id + " auf ("
				+ x + ", " + y + ")");
		model.setPosition(id, Integer.parseInt(x), Integer.parseInt(y));
	}

	@Override 
	public void setName(final String id, final String name) {
		System.out.println("Setze den Namen des Elements " + id + " auf "
				+ name);
		model.setName(id, name);
	}

	@Override 
	public void setTokens(final String id, final String tokens) {
		System.out.println("Setze die Markenanzahl des Elements " + id
				+ " auf " + tokens);
		model.setTokens(id, Integer.parseInt(tokens));
	}
}
