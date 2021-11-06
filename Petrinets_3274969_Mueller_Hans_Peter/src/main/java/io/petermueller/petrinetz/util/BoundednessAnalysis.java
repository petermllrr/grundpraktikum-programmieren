package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.petrinet.Transition;
import io.petermueller.petrinetz.models.rgraph.Marking;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;

public class BoundednessAnalysis {
	public boolean isBounded;
	public Marking m1;
	public Marking m2;
	public List<TransitionArc> detectionPath;
	public PetriNetModel petriNet;
	public RGraphModel rGraph;

	public BoundednessAnalysis(PetriNetModel inputPetriNet) {
		this.m1 = null;
		this.m2 = null;
		this.detectionPath = new ArrayList<TransitionArc>();
		this.petriNet = new PetriNetModel();
		this.rGraph = new RGraphModel(this.petriNet);
		petriNet.load(inputPetriNet);
		this.isBounded = isBounded();
	}

	private boolean isBounded() {
		this.petriNet.reset();
		this.rGraph.reset(petriNet.places); //TODO als reset event on load
		List<Marking> visitedQueue = new ArrayList<Marking>();
		List<Marking> searchQueue = new ArrayList<Marking>();
		Marking currentMarking = new Marking(petriNet.places);
		searchQueue.add(currentMarking);
		boolean isBounded = traversePetriNetBreadthFirst(
				visitedQueue,
				searchQueue);
		if (!isBounded) {
			setRGraphToAnalysisGraph();
			setPetriNetToAbortMarking();
		}
		return isBounded;
	}

	private boolean traversePetriNetBreadthFirst(
			List<Marking> visitedQueue,
			List<Marking> searchQueue) {
		while (!searchQueue.isEmpty()) {
			Marking currentMarking = searchQueue.get(0); // .removeFirst
			searchQueue.remove(0);
			if (hasUnboundednessCondition(currentMarking, visitedQueue)) {
				return false;
			} else if (!graphIsCircular(currentMarking, visitedQueue)) {
				visitedQueue.add(currentMarking);
				petriNet.setToMarking(currentMarking.places);
				List<Marking> reachableMarkings = getReachableMarkings();
				for (Marking marking : reachableMarkings) {
					searchQueue.add(searchQueue.size(), marking);
				}
			}
		}	
		return true;
	}

	private boolean hasUnboundednessCondition(
			Marking current,
			List<Marking> visitedQueue) {
		for (Marking visited : visitedQueue) {
			if (hasAtLeastAsMuchTokens(current, visited) &&
					hasOneTokenMore(current, visited) &&
					isReachable(visited, current, rGraph)) {
				this.m1 = visited;
				this.m2 = current;
				return true;
			}
		}
		return false;
	}

	private boolean hasAtLeastAsMuchTokens(
			Marking current,
			Marking visited) {
		boolean result = true;
		for (int i = 0; i < visited.places.size(); i++) {
			int currentPlaceTokens = current.places.get(i).currentTokens;
			int visitedPlaceTokens = visited.places.get(i).currentTokens;
			if (currentPlaceTokens < visitedPlaceTokens) {
				result = false;
			}
		}
		return result;
	}

	private boolean hasOneTokenMore(
			Marking current,
			Marking visited) {
		boolean result = false;
		for (int i = 0; i < visited.places.size(); i++) {
			int currentPlaceTokens = current.places.get(i).currentTokens;
			int visitedPlaceTokens = visited.places.get(i).currentTokens;
			if (currentPlaceTokens > visitedPlaceTokens) {
				result = true;
			}
		}
		return result;
	}

	private boolean isReachable(
			Marking sourceMarking,
			Marking targetMarking,
			RGraphModel rGraph) {
		List<Marking> visited = new ArrayList<Marking>();
		return traverseRGraphDepthFirst(
				sourceMarking,
				targetMarking,
				visited);
	};

	private boolean traverseRGraphDepthFirst(
			Marking sourceMarking,
			Marking targetMarking,
			List<Marking> visited) {
		boolean result = false;
		if (!graphIsCircular(sourceMarking, visited)) {
			List<Marking> visitedRecursive = new ArrayList<Marking>(visited);
			visitedRecursive.add(sourceMarking);
			for (TransitionArc arc : rGraph.arcs) {
				if (arc.source.id.equals(sourceMarking.id)) {
					if (arc.target.id.equals(targetMarking.id)) {
						visitedRecursive.add(targetMarking);
						this.detectionPath = getDetectionPath(visitedRecursive);
						return true;
					} else {
						result = traverseRGraphDepthFirst(
								arc.target,	
								targetMarking,
								visitedRecursive);
					}
				}
			}
		}
		return result;
	}

	private List<TransitionArc> getDetectionPath(
			List<Marking> visitedMarkings) {
		List<TransitionArc> path = new ArrayList<TransitionArc>();
		int indexMax = visitedMarkings.size() - 1;
		for (int i = 0; i < indexMax; i ++) {
			for (TransitionArc arc : rGraph.arcs) {
				if (arc.source.id.equals(visitedMarkings.get(i).id) &&
						arc.target.id.equals(visitedMarkings.get(i + 1).id)) {
					path.add(arc);
				}
			}
		}
		return path;
	}

	private void setRGraphToAnalysisGraph() {
		removeActiveMarking();
		insertDetectionPath();
		insertStartAndEndMarking();
	}

	private void insertDetectionPath() {
		for (TransitionArc pathArc : this.detectionPath) {
			pathArc.isOnDetectionPath = true;
		}
	}

	private void removeActiveMarking() {
		for (Marking marking : rGraph.markings) {
			marking.isActive = false;
		}
	}

	private void insertStartAndEndMarking() {
		for (Marking marking : rGraph.markings) {
			if (marking.id.equals(m1.id)) {
				marking.isPathStart = true;
			}
			if (marking.id.equals(m2.id)) {
				marking.isPathEnd = true;
			}
		}
	}

	private void setPetriNetToAbortMarking() {
		petriNet.setToMarking(m2.places);
	}

	private List<Marking> getReachableMarkings() {
		Marking currentMarking = new Marking(petriNet.places);
		List<Marking> reachableMarkings = new ArrayList<Marking>();
		List<Transition> enabledTransitions = new ArrayList<Transition>();
		for (Transition transition : petriNet.transitions) {
			if (transition.isEnabled) {
				enabledTransitions.add(transition);
			}
		}
		for (Transition transition : enabledTransitions) {
			petriNet.setToMarking(currentMarking.places);
			petriNet.fire(transition);
			Marking marking = new Marking(petriNet.places);
			reachableMarkings.add(marking);
		}
		petriNet.setToMarking(currentMarking.places);
		return reachableMarkings;
	}

	private boolean graphIsCircular(
			Marking currentMarking,
			List<Marking> visitedQueue) {
		boolean result = false;
		for (Marking queueElement : visitedQueue) {
			if (markingIsEqual(queueElement, currentMarking)) {
				result = true;
			}
		}
		return result;
	}

	private boolean markingIsEqual(
			Marking marking1,
			Marking marking2) {
		boolean result = true;
		for (int i = 0; i < marking1.places.size(); i++) {
			if (!marking1.id.equals(marking2.id)) return false;
		}
		return result;
	}
}
