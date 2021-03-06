package io.petermueller.petrinetz.models.filesystem;

import java.io.File;
import propra.pnml.PNMLWopedParser;
import io.petermueller.petrinetz.models.petrinet.*;

/**
 * Parses PNML files and sends parsed information to the {@link PetriNetModel}.
 * This class is an extension of the Woped PNML Parser which was provided in
 * the demo project of the Praktikum.
 * 
 * @author Hans Peter Müller (3274969) and Praktikumsbetreuung FernUni Hagen
 */
public class PNMLParser extends PNMLWopedParser {
	private PetriNetModel model;

	/**
	 * Initializes the parser.
	 * 
	 * @param file the file to parse
	 * @param model the {@link PetriNetModel} in which the parsed information
	 * should be saved
	 */
	public PNMLParser(File file, PetriNetModel model) {
		super(file);
		this.model = model;
	}

	/**
	 * Adds a transition to the model.
	 */
	@Override 
	public void newTransition(final String id) {
		model.addTransition(id);
	}

	/**
	 * Adds a place to the model.
	 */
	@Override 
	public void newPlace(final String id) {
		model.addPlace(id);
	}

	/**
	 * Adds an arc to the model.
	 */
	@Override 
	public void newArc(
			final String id,
			final String source,
			final String target) {
		model.addArc(id, source, target);
	}

	/**
	 * Sets the x and y coordinates of a place or a transition.
	 */
	@Override 
	public void setPosition(final String id, final String x, final String y) {
		model.setPosition(id, Integer.parseInt(x), Integer.parseInt(y));
	}

	/**
	 * Sets a name for a place or transition.
	 */
	@Override 
	public void setName(final String id, final String name) {
		model.setName(id, name);
	}

	/**
	 * Sets the amount of tokens of a place.
	 */
	@Override 
	public void setTokens(final String id, final String tokens) {
		model.setTokens(id, Integer.parseInt(tokens));
	}
}
