package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;

public class BatchProcessing {
	public List<BoundednessAnalysis> analysisList;

	public BatchProcessing(List<PetriNetModel> petriNets) {
		analysisList = new ArrayList<BoundednessAnalysis>();
		for (PetriNetModel petriNet : petriNets) {
			BoundednessAnalysis analysis = new BoundednessAnalysis(petriNet);
			analysisList.add(analysis);
		}
	}
}