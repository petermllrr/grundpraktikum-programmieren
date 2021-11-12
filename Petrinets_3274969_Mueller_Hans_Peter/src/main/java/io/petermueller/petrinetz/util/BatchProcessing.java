package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;

/**
 * Runs a {@link BoundednessAnalysis boundedness analysis} for multiple
 * {@link PetriNetModel Petri nets} and stores the results for easy access.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class BatchProcessing {
	/**
	 * List of {@link BoundednessAnalysis boundedness analysis} results.
	 */
	public List<BoundednessAnalysis> analysisList;

	/**
	 * Runs boundedness analysis in a batch procession fashion for the given
	 * list of {@link PetriNetModel Petri nets}.
	 * 
	 * @param petriNets a list of {@link PetriNetModel Petri nets}
	 */
	public BatchProcessing(List<PetriNetModel> petriNets) {
		analysisList = new ArrayList<BoundednessAnalysis>();
		for (PetriNetModel petriNet : petriNets) {
			BoundednessAnalysis analysis = new BoundednessAnalysis(petriNet);
			analysisList.add(analysis);
		}
	}
}
