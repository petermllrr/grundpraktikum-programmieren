package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.petrinet.Transition;
import io.petermueller.petrinetz.models.rgraph.Marking;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;

/**
 * Runs a boundedness analysis of a {@link PetriNetModel Petri net}. The
 * result is stored as {@link #isBounded}. Should a Petri net
 * be unbounded, the abort criteria can be retrieved from this object as well.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class BoundednessAnalysis {
	/**
	 * {code true}, if the {@link PetriNetModel Petri net} is bounded,
	 * {@code false} otherwise.
	 */
	public boolean isBounded;
	/**
	 * The start {@link Marking} (m) of the detection path of the boundedness
	 * algorithm. It's only stored if the {@link PetriNetModel Petri net} is
	 * unbounded.
	 */
	public Marking m1;
	/**
	 * The end {@link Marking} (m') of the detection path of the boundedness
	 * algorithm. It's only stored if the {@link PetriNetModel Petri net} is
	 * unbounded.
	 */
	public Marking m2;
	/**
	 * The path from {@link BoundednessAnalysis#m1 m} to
	 * {@link BoundednessAnalysis#m2 m'}in the
	 * {@link RGraphModel reachability graph}.
	 */
	public List<TransitionArc> detectionPath;
	/**
	 * The {@link PetriNetModel Petri net} which is analyzed.
	 */
	public PetriNetModel petriNet;
	/**
	 * The constructed {@link RGraphModel reachability graph} of the analysis.
	 */
	public RGraphModel rGraph;

	/**
	 * Initializes a boundedness analysis of a {@link PetriNetModel Petri net}.
	 * The results and additional information in case the Petri net is
	 * unbounded is stored in the object.
	 * 
	 * @param inputPetriNet the {@link PetriNetModel Petri net} to analyze
	 */
	public BoundednessAnalysis(PetriNetModel inputPetriNet) {
		this.m1 = null;
		this.m2 = null;
		this.detectionPath = new ArrayList<TransitionArc>();
		this.petriNet = new PetriNetModel();
		this.rGraph = new RGraphModel(this.petriNet);
		petriNet.load(inputPetriNet);
		this.isBounded = isBounded();
	}

	/**
	 * Starts the boundedness algorithm for the {@link PetriNetModel} stored in
	 * this BoundednessAnalysis object.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * This method initializes a breadth-first search through all possible
	 * markings in a Petri net. To make the code easier to read, the actual
	 * breadth-first search algorithm is implemented in the
	 * {@link #traversePetriNetBreadthFirst} method.
	 *  
	 * @return {@code true} if the {@link PetriNetModel Petri net} is bounded,
	 *         {@code false} otherwise
	 */
	private boolean isBounded() {
		this.petriNet.reset();
		this.rGraph.reset(petriNet.places);
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

	/**
	 * Moves through all possible markings of a {@link PetriNetModel Petri net}
	 * breadth-first and performs an unboundedness check for each {@link Marking
	 * marking} visited.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * The algorithm picks the first marking from the {@code searchQueue}
	 * (initially start marking of the Petri net) and
	 * checks for the unboundedness condition or if this marking has already
	 * been visited. If both conditions don't apply, all possible
	 * {@link Transition transitions} in the Petri net are fired and the newly
	 * reached markings are added to the {@code searchQueue} and the
	 * reachability graph. This process now repeats for all markings in the
	 * {@code searchQueue}.
	 * 
	 * <p>By doing so, the {@link RGraphModel reachability graph} is built up
	 * step by step in a breadth-first way. Already visited markings are stored
	 * in the {@code visitedQueue} so the algorithm can detect if it's running
	 * in a circle.
	 * 
	 * <p>The search terminates with {@code false} if the unboundedness
	 * condition has been met or with {@code true} if all markings have been
	 * visited.
	 * 
	 * @param visitedQueue the markings which have been already
	 *                     visited by the algorithm
	 * @param searchQueue  the markings which the algorithm
	 *                     still needs to analyze
	 * @return             {@code true} if the {@link PetriNetModel} is bounded,
	 *                     {@code false} otherwise
	 */
	private boolean traversePetriNetBreadthFirst(
			List<Marking> visitedQueue,
			List<Marking> searchQueue) {
		while (!searchQueue.isEmpty()) {
			Marking currentMarking = searchQueue.remove(0);
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

	/**
	 * Checks, if a marking m and m' fulfill the unboundedness conditions. These
	 * are: (1) m' has at least as many markers as m, (2) m' has at least one
	 * more marker as m and (3) m' is reachable from m.
	 * 
	 * @param current      the marking m'
	 * @param visitedQueue list of all already visited markings
	 * @return             {@code true} if there is a marking m and m' that
	 *                     fulfill all conditions of the unboundedness check,
	 *                     {@code false} otherwise
	 */
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

	/**
	 * Checks if a {@link Marking} m' has at least as many markers on each
	 * place as a marking m.
	 * 
	 * @param current a marking m'
	 * @param visited a marking m
	 * @return        {@code true} if m' has as many markings as m,
	 *                {@code false} otherwise
	 */
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

	/**
	 * Checks if a {@link Marking} m' has at least one marker more on a place
	 * than marking m.
	 * 
	 * @param current a marking m'
	 * @param visited a marking m
	 * @return        {@code true} if m' has at least one more marker on a place
	 *                than m, {@code false} otherwise
	 */
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

	/**
	 * Checkfs if a {@link Marking} m' is reachable from another marking m in a
	 * given {@link RGraphModel reachability graph}.
	 * 
	 * <p>This method is a wrapper for a recursive depth-first search.
	 * 
	 * @param sourceMarking a marking m'
	 * @param targetMarking a marking m
	 * @param rGraph        a reachability graph
	 * @return              {@code true} if m' is reachable from m,
	 *                      {@code false} otherwise
	 */
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

	/**
	 * Depth-first search inside a {@link RGraphModel reachability graph} to
	 * check if a given {@link Marking} m is reachable from another given
	 * marking m'.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * The algorithm tries to find a path between the markings m and m'. It
	 * checks all outgoing {@link Arc arcs} of m (referenced as
	 * {@code sourceMarking}).
	 * 
	 * <p>The search terminates and returns {@code true}, if the destination of
	 * an arc is m' (references as {@code targetMarking}). A reference of the
	 * path from m to m' is stored in {@code detectionPath}. If the destination
	 * of an arc is not m', the algorithm continues recursively from this
	 * destination marking.
	 * 
	 * @param sourceMarking m'
	 * @param targetMarking m
	 * @param visited       a list of already visited markings
	 * @return              {@code true} if m' is reachable from m,
	 *                      {@code false} otherwise
	 */
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

	/**
	 * Generates a list of {@link TransitionArc TransitionArcs} which form a
	 * path along a list of {@link Marking markings}.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * The method checks each {@link TransitionArc} in the
	 * {@link RGraphModel} if the source and the target match the markings in
	 * the list of {@code visitedMarkings}. If they match, the arc is
	 * added to the {@code path} and returned.
	 * 
	 * @param visitedMarkings a list of connected markings in
	 *                        the {@link RGraphModel reachability graph}
	 * @return                a list of arcs which form a path along markings
	 */
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

	/**
	 * Adds additional information to the {@link RGraphModel} after the
	 * unboundedness criteria have been met.
	 */
	private void setRGraphToAnalysisGraph() {
		rGraph.removeActiveMarking();
		insertDetectionPath();
		insertStartAndEndMarking();
	}

	/**
	 * Tags {@link TransitionArc TransitionArcs} in the {@link RGraphModel} if
	 * they are on the path from {@link Marking} m to m'.
	 */
	private void insertDetectionPath() {
		for (TransitionArc pathArc : this.detectionPath) {
			pathArc.isOnDetectionPath = true;
		}
	}

	/**
	 * Tags {@link Marking markings} in the {@link RGraphModel} as the start and
	 * end node of the path from m to m'.
	 */
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

	/**
	 * Sets the {@link PetriNetModel} of this analysis to the {@link Marking} of
	 * m'.
	 */
	private void setPetriNetToAbortMarking() {
		petriNet.setToMarking(m2.places);
	}

	/**
	 * Returns all possible {@link Marking Markings} that can be reached from
	 * a {@link PetriNetModel Petri net}.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * The method takes the {@link PetriNetModel} associated with this
	 * boundedness analysis. All enabled transition are stored in the 
	 * {@code enabledTransitions} list. Each of these transitions is fired in
	 * turn and the resulting marking is added to the {@code reachableMarkings}
	 * list.
	 * 
	 * <p>Before each firing, the Petri net is reset to the original marking.
	 * This is also repeated after the method has been run, to leave the
	 * Petri net in the same state as it was before.
	 * 
	 * @return a list of markings that can be reached from the
	 *         {@link PetriNetModel} in this analysis
	 */
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

	/**
	 * Checks if a {@link Marking} is contained in a list of markings. This is
	 * used to check if a marking has already been visited, to avoid
	 * that the breadth-first search runs in circles.
	 * 
	 * @param currentMarking a marking
	 * @param visitedQueue   a list of markings already visited
	 * @return               {@code true} if the marking is contained in the
	 *                       list, {@code false} otherwise
	 */
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

	/**
	 * Compares two markings and checks if they are equal.
	 * 
	 * @param marking1 the first marking
	 * @param marking2 the second marking
	 * @return         {@code true} if the markings are equal, {@code false}
	 *                 otherwise
	 */
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
