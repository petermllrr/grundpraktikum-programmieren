package io.petermueller.petrinetz.util;

import java.util.ArrayList;
import java.util.List;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.petrinet.Transition;
import io.petermueller.petrinetz.models.rgraph.Marking;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;

/**
 * Runs a boundedness analysis of a Petri net. The
 * result is stored as {@link #isBounded}. Should a Petri net
 * be unbounded, the abort criteria can be retrieved from this object as well.
 * 
 * @author Hans Peter Müller (3274969)
 * @see    PetriNetModel
 */
public class BoundednessAnalysis {
	/**
	 * {@code true} if the Petri net is bounded, {@code false} otherwise.
	 * 
	 * @see PetriNetModel
	 */
	public boolean isBounded;
	/**
	 * The start marking {@code m} of the termination path of the boundedness
	 * algorithm. Null, if the Petri net is bounded.
	 * 
	 * @see Marking
	 * @see PetriNetModel
	 */
	public Marking m1;
	/**
	 * The end marking {@code m'} of the termination path of the boundedness
	 * algorithm. Null, if the Petri net is bounded.
	 * 
	 * @see Marking
	 * @see PetriNetModel
	 */
	public Marking m2;
	/**
	 * The path from {@code m} to {@code m'} in the reachability graph.  Null,
	 * if the Petri net is bounded.
	 * 
	 * @see BoundednessAnalysis#m1
	 * @see BoundednessAnalysis#m2
	 * @see RGraphModel
	 */
	public List<TransitionArc> terminationPath;
	/**
	 * Reference to the Petri net which is analyzed.
	 * 
	 * @see PetriNetModel
	 */
	public PetriNetModel petriNet;
	/**
	 * The constructed reachability graph of the analysis.
	 * 
	 * @see RGraphModel
	 */
	public RGraphModel rGraph;

	/**
	 * Initializes a boundedness analysis of a Petri net. The result (and
	 * additional information if the Petri net is unbounded) is stored as object
	 * attributes.
	 * 
	 * @param inputPetriNet the Petri net to analyze
	 * @see PetriNetModel
	 */
	public BoundednessAnalysis(PetriNetModel inputPetriNet) {
		this.m1 = null;
		this.m2 = null;
		this.terminationPath = new ArrayList<TransitionArc>();
		this.petriNet = new PetriNetModel();
		this.rGraph = new RGraphModel(this.petriNet);
		petriNet.load(inputPetriNet);
		this.isBounded = isBounded();
	}

	/**
	 * Starts the boundedness algorithm for the Petri net stored in
	 * {@link BoundednessAnalysis#petriNet}.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * This method starts a breadth-first search through all possible markings
	 * of the Petri net {@code BoundednessAnalysis.petriNet}. To make the
	 * algorithm easier to read, the actual breadth-first search code sits in
	 * the {@code traversePetriNetBreadthFirst} method.
	 *  
	 * @return {@code true} if the Petri net is bounded, {@code false} otherwise     
	 * @see    PetriNetModel
	 * @see    BoundednessAnalysis#traversePetriNetBreadthFirst
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
	 * Builds up all possible markings of a Petri net by a breadth-first search
	 * and performs a boundedness analysis for each marking.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * The algorithm selects the first marking from the {@code searchQueue}
	 * (at the beginning of the analysis this is the start marking of the Petri
	 * net) and checks whether the unboundedness conditions for this marking is
	 * satisfied and whether this marking has already been visited.
	 * 
	 * <p>If both are not true, all possible transitions in the Petri net are
	 * fired and newly reached markings are added to the {@code searchQueue}.
	 * All previously visited markings are added to the {@code visitedQueue} so
	 * that the algorithm can check whether a marking has already been visited.
	 * This process is now repeated for all new markings in the
	 * {@code searchQueue}.
	 * 
	 * <p>By this procedure, the reachability graph builds up successively in a
	 * breadth-first search. The search terminates with {@code true} as soon as
	 * the unboundedness conditions are met by a marking, or with {@code false}
	 * if all possible markings have been visited and the conditions have not
	 * been met for any of them.
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
	 * Checks, if a marking m and m' satisfy the unboundedness conditions. These
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
	 * Checks if a marking m' has at least as many markers on each place as a
	 * marking m.
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
	 * Checks if a marking m' has at least one marker more on a place than 
	 * marking m.
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
	 * Checkfs if a Marking m' is reachable from another marking m in a
	 * given reachability graph.
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
	 * The algorithm tries to find a path between two markings m and m' in the
	 * reachability graph. To do this, it starts from marking m
	 * ({@code souceMarking}) and checks whether any outgoing edge (transition)
	 * leads to marking m' ({@code targetMarking}).
	 * 
	 * <p>If this is not the case, this search is performed recursively for all
	 * outgoing markings of m. The search terminates with {@code true} if m' is
	 * found or {@code false} if all markings have been checked and m' could not
	 * be reached. If m' is found, the termination path is saved as well for
	 * later representation in the GUI.
	 * 
	 * @param sourceMarking m'
	 * @param targetMarking m
	 * @param visited       a list of already visited markings
	 * @return              {@code true} if m' is reachable from m,
	 *                      {@code false} otherwise
	 * @see                 BoundednessAnalysis#terminationPath
	 */
	private boolean traverseRGraphDepthFirst(
			Marking sourceMarking,
			Marking targetMarking,
			List<Marking> visited) {
		boolean result = false;
		if (!graphIsCircular(sourceMarking, visited)) {
			/*
			 * {@code visitedRecursive} is a helper variable to ensure that each
			 * recursive function call stores its own path and the functions do
			 * not share a common variable for the path.
			 */
			List<Marking> visitedRecursive = new ArrayList<Marking>(visited);
			visitedRecursive.add(sourceMarking);
			for (TransitionArc arc : rGraph.arcs) {
				if (arc.source.id.equals(sourceMarking.id)) {
					if (arc.target.id.equals(targetMarking.id)) {
						visitedRecursive.add(targetMarking);
						this.terminationPath = getTerminationPath(
								visitedRecursive);
						/*
						 * Here the recursive search terminates with
						 * {@code true}.
						 */
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
		/*
		 * If the target marking is not found an no further recursive functions
		 * are being created, the search returns {@code false}.
		 */
		return result;
	}

	/**
	 * Creates a list of {@code TransitionArcs} that result in a path along a
	 * given list of markings.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * {@code visitedMarkings} is the list of markings along which a path
	 * should be created. The method checks each {@code TransitionArc} in the
	 * {@code RGraphModel} to see if it represents a connection between two
	 * adjacent markings in the list. If so, it is added to a list of
	 * {@code TransitionArcs} and returned.
	 * 
	 * @param visitedMarkings a list of connected markings in
	 *                        the {@link RGraphModel reachability graph}
	 * @return                a list of arcs which form a path along markings
	 */
	private List<TransitionArc> getTerminationPath(
			List<Marking> visitedMarkings) {
		List<TransitionArc> path = new ArrayList<TransitionArc>();
		int indexMax = visitedMarkings.size() - 1;
		for (int i = 0; i < indexMax; i ++) {
			for (TransitionArc arc : rGraph.arcs) {
				/**
				 * We check if a transition is a connection between a marking
				 * with index i and a marking with index i + 1.
				 */
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
		insertTerminationGraph();
		insertStartAndEndMarking();
	}

	/**
	 * Tags {@code TransitionArcs} in the {@code RGraphModel} if
	 * they are on the termination path from marking m to m'.
	 * 
	 * @see BoundednessAnalysis#terminationPath
	 */
	private void insertTerminationGraph() {
		for (TransitionArc pathArc : this.terminationPath) {
			pathArc.isOnDetectionPath = true;
		}
	}

	/**
	 * Tags markings in the {@link RGraphModel} as the start and
	 * end node of the termination path from m to m'.
	 * 
	 * @see BoundednessAnalysis#terminationPath
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

	/*
	 * Sets the {@link PetriNetModel} of this analysis to the marking of
	 * m'. The method is just syntactic sugar to make above function calls more
	 * readable.
	 */
	private void setPetriNetToAbortMarking() {
		petriNet.setToMarking(m2.places);
	}

	/**
	 * Detects all active transitions in a Petri net and returns all reachable
	 * markings.
	 * 
	 * <p><strong>Implementation details:</strong><br>
	 * This method searches the Petri net {@code BoundednessAnalysis.petriNet}
	 * and stores all active transitions in a list {@code enabledTransitions}.
	 * Then each of these transitions is fired in turn and the new Petri net
	 * markings are stored in a list {@code reachableMarkings} which is finally
	 * returned.

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
			/*
			 * We need to reset the Petri net each time to the current marking
			 * before firing it.
			 */
			petriNet.setToMarking(currentMarking.places);
			petriNet.fire(transition);
			Marking marking = new Marking(petriNet.places);
			reachableMarkings.add(marking);
		}
		/**
		 * Before terminating the method, we need to reset the Petri net once
		 * more so we leave it in the same state as we found it.
		 */
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
			if (currentMarking.id.equals(queueElement.id)) return true;
		}
		return result;
	}
}