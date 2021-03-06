package io.petermueller.petrinetz.models.rgraph;

/**
 * Represents a transition from one {@link Marking} to another {@link Marking} 
 * in the {@link RGraphModel}. 
 * 
 * @author Hans Peter Müller (3274969)
 */
public class TransitionArc {
	/**
	 * The source {@link Marking} from which the transition starts.
	 */
	public Marking source;
	/**
	 * The target {@link Marking} to which the transition points.
	 */
	public Marking target;
	/**
	 * The unique id of the transition.
	 */
	public String id;
	/**
	 * A short id, used for text output of the batch analysis.
	 */
	public String shortId;
	/**
	 * True, if this transition is the last one fired by the
	 * {@link io.petermueller.petrinetz.models.petrinet.PetriNetModel 
	 * Petri net}.
	 */
	public boolean isLatest;
	/**
	 * True, if this transition is on the detection path of the boundedness
	 * analysis.
	 */
	public boolean isOnDetectionPath;

	/**
	 * Initializes a transition.
	 * 
	 * @param id      a unique id
	 * @param shortId a short id
	 * @param source  a reference to the source {@link Marking}
	 * @param target  a reference to the target {@link Marking}
	 */
	public TransitionArc(
			String id,
			String shortId,
			Marking source,
			Marking target) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.isOnDetectionPath = false;
		this.shortId = shortId;
	}
}
