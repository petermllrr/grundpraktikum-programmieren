package io.petermueller.petrinetz.util;

/**
 * Event types that are used in this app to keep its different components in
 * sync.
 * 
 * @author Hans Peter Müller (3274969)
 */
public enum Event {
	/**
	 * The file system model has read a new file and handed it to the Petri net
	 * model.
	 */
	FILESYSTEM_NEW_FILE_OPENED,
	/**
	 * The filesystem model has read an invalid file.
	 */
	FILESYSTEM_INVALID_FILE,
	/**
	 * The Petri net model has fired a transition.
	 */
	PETRINET_TRANSITION_FIRED,
	/**
	 * The Petri net model has a new selected place.
	 */
	PETRINET_USER_SELECTED_A_PLACE,
	/**
	 * The Petri net model has resetted it's marking to the start marking.
	 */
	PETRINET_MARKING_RESET,
	/**
	 * The Petri net model has a marking that is different from the initial
	 * start marking when the file was read.
	 */
	PETRINET_MARKING_EDITED,
	/**
	 * The Petri net model has loaded a new Petri net model.
	 */
	PETRINET_NEW_NET_LOADED,
	/**
	 * The reachability graph model has cleared its content and set a new
	 * marking as the root marking.
	 */
	RGRAPH_RESET,
	/**
	 * The reachability graph model has set itself to a new active marking.
	 */
	RGRAPH_SET_TO_MARKING,
	/**
	 * The reachability graph model has loaded a new model.
	 */
	RGRAPH_NEW_GRAPH_LOADED;
}
