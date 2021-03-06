/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.rgraph.RGraphModel;

/**
 * @author petermueller
 *
 */
@DisplayName("Reachability Graph Model")
class RGraphModelTest {
	private RGraphModel rGraph;
	private PetriNetModel petriNet;

	@BeforeEach
	void setUp() throws Exception {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 3);
		petriNet.addPlace("p2");
		petriNet.setPosition("p2", 5000, 3);
		petriNet.setName("p2", "place 2");
		petriNet.addTransition("t1");
		petriNet.setName("t1", "first");
		petriNet.addTransition("t2");
		petriNet.setName("t2", "second");
		petriNet.addArc("a1", "p1", "t1");
		petriNet.addArc("a2", "t1", "p2");
		petriNet.addArc("a3", "p2", "t2");
		petriNet.addArc("a4", "t2", "p1");
		rGraph = new RGraphModel(petriNet);
	}

	@Test
	@DisplayName("adds a new marking")
	void addsMarking() {

		rGraph.addMarking(petriNet.places);
		assertEquals("(3|0)", rGraph.markings.get(0).id);
	}

	@Test
	@DisplayName("doesn't add two equal markings")
	void noEqualMarkings() {
		rGraph.addMarking(petriNet.places);
		rGraph.addMarking(petriNet.places);
		assertEquals(1, rGraph.markings.size());
	}

	@Test
	@DisplayName("adds a new marking and a transition after Petri net fires")
	void addsTransition() {
		petriNet.fire(petriNet.transitions.get(0));
		assertAll("adds transition",
				() -> assertEquals(2, rGraph.markings.size()),
				() -> assertEquals(1, rGraph.arcs.size()),
				() -> assertEquals("(3|0)", rGraph.markings.get(0).id),
				() -> assertEquals("(2|1)", rGraph.markings.get(1).id),
				() -> assertEquals("[t1] first", rGraph.arcs.get(0).id),
				() -> assertTrue(rGraph.markings.get(1).isActive)
				);
	}

	@Test
	@DisplayName("adds a new transition even if there is already one " +
			"between two markings")
	void additionalTransition() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 3);
		petriNet.addPlace("p2");
		petriNet.setPosition("p2", 5000, 3);
		petriNet.setName("p2", "place 2");
		petriNet.addTransition("t1");
		petriNet.setName("t1", "first");
		petriNet.addTransition("t2");
		petriNet.setName("t2", "second");
		petriNet.addArc("a1", "p1", "t1");
		petriNet.addArc("a2", "t1", "p2");
		petriNet.addArc("a3", "p1", "t2");
		petriNet.addArc("a4", "t2", "p2");
		rGraph = new RGraphModel(petriNet);
		petriNet.fire(petriNet.transitions.get(0));
		petriNet.setTokens("p1", 3);
		petriNet.setTokens("p2", 0);
		petriNet.fire(petriNet.transitions.get(1));
		assertAll("adds transition",
				() -> assertEquals(2, rGraph.markings.size()),
				() -> assertEquals(2, rGraph.arcs.size()),
				() -> assertEquals("(3|0)", rGraph.markings.get(0).id),
				() -> assertEquals("(2|1)", rGraph.markings.get(1).id),
				() -> assertEquals("[t1] first", rGraph.arcs.get(0).id),
				() -> assertEquals("[t2] second", rGraph.arcs.get(1).id),
				() -> assertTrue(rGraph.markings.get(1).isActive)
				);
	}

	@Test
	@DisplayName("resets")
	void resets() {
		PetriNetModel petriNet2 = new PetriNetModel();
		petriNet2 = new PetriNetModel();
		petriNet2.addPlace("p1");
		petriNet2.setName("p1", "place 1");
		petriNet2.setTokens("p1", 3);
		petriNet2.addPlace("p2");
		petriNet2.setPosition("p2", 5000, 3);
		petriNet2.setName("p2", "place 2");
		petriNet2.addTransition("t1");
		petriNet2.setName("t1", "first");
		petriNet2.addTransition("t2");
		petriNet2.setName("t2", "second");
		petriNet2.addArc("a1", "p1", "t1");
		petriNet2.addArc("a2", "t1", "p2");
		petriNet2.addArc("a3", "p2", "t2");
		petriNet2.addArc("a4", "t2", "p1");
		petriNet.fire(petriNet.transitions.get(0));
		rGraph.reset(petriNet2.places);
		assertAll("resets",
				() -> assertEquals(1, rGraph.markings.size()),
				() -> assertEquals(0, rGraph.arcs.size()),
				() -> assertEquals("(3|0)", rGraph.markings.get(0).id)
				);	
	}

	@Test
	@DisplayName("sets the currently active marking")
	void setsMarking() {
		petriNet.fire(petriNet.transitions.get(0));
		assertTrue(rGraph.markings.get(1).isActive);
	}

	@Test
	@DisplayName("gets marking by id")
	void getsMarking() {
		petriNet.fire(petriNet.transitions.get(0));
		assertEquals("(2|1)", rGraph.getMarkingById("(2|1)").id);
	}

	@Test
	@DisplayName("loads another RGraphModel")
	void loads() {
		PetriNetModel petriNet2 = new PetriNetModel();
		petriNet2.addPlace("q1");
		petriNet2.setName("q1", "qplace 1");
		petriNet2.setTokens("q1", 3);
		petriNet2.addPlace("q2");
		petriNet2.setPosition("q2", 5000, 3);
		petriNet2.setName("q2", "qplace 2");
		petriNet2.addTransition("u1");
		petriNet2.setName("u1", "u1 transition");
		petriNet2.addTransition("u2");
		petriNet2.setName("u2", "u2 transition");
		petriNet2.addArc("b1", "q1", "u1");
		petriNet2.addArc("b2", "u1", "q2");
		petriNet2.addArc("b3", "q1", "u1");
		petriNet2.addArc("b4", "u1", "q2");
		RGraphModel rGraph2 = new RGraphModel(petriNet2);
		rGraph.load(rGraph2);
		assertAll("loads",
				() -> assertEquals(1, rGraph.markings.size()),
				() -> assertEquals(0, rGraph.arcs.size()),
				() -> assertEquals("(3|0)", rGraph.markings.get(0).id)
				);
	}
}
