package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;

/**
 * Performs a boundedness analysis for multiple Petri nets and saves the
 * results.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    BoundednessAnalysis
 * @see    PetriNetModel
 */
public class BatchProcessing {
	/**
	 * List of {@link BoundednessAnalysis boundedness analysis} results.
	 */
	public List<BoundednessAnalysis> analysisList;

	/**
	 * Performs boundedness analyses with a given list of Petri nets.
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
