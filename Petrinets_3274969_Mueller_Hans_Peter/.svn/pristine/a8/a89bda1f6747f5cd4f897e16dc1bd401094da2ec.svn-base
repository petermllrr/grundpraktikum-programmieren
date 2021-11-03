package tests;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.*;

/**
 * 
 */

/**
 * @author petermueller
 *
 */
@DisplayName("Petri Net Model")
class PetriNetModelTest {
	private PetriNetModel petriNet;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		petriNet = new PetriNetModel();
	}

	@Nested
	@DisplayName("Transitions")
	class Transitions {
		@BeforeEach
		void beforeAll() {
			petriNet = new PetriNetModel();
			petriNet.addTransition("t1");
			petriNet.setPosition("t1", 3, 9000);
			petriNet.setName("t1", "send mail");
			petriNet.addTransition("t2");
			petriNet.setPosition("t2", 5000, 3);
			petriNet.setName("t2", "receive mail");
		}

		@Test
		@DisplayName("are loaded")
		void correctAmount() {
			assertEquals(2, petriNet.transitions.size());
		}

		@Test
		@DisplayName("have the correct id")
		void correctId() {
			assertAll("ids",
					() -> assertEquals("t1", petriNet.transitions.get(0).id),
					() -> assertEquals("t2", petriNet.transitions.get(1).id)
					);
		}

		@Test
		@DisplayName("have the correct name")
		void correctName() {
			assertAll("names",
					() -> assertEquals(
							"send mail", petriNet.transitions.get(0).name),
					() -> assertEquals(
							"receive mail", petriNet.transitions.get(1).name)
					);
		}

		@Test
		@DisplayName("have the correct coordinates")
		void correctCoordinates() {
			assertAll("coordinates",
					() -> assertEquals(
							3, petriNet.transitions.get(0).x),
					() -> assertEquals(
							-9000, petriNet.transitions.get(0).y),
					() -> assertEquals(
							5000, petriNet.transitions.get(1).x),
					() -> assertEquals(
							-3, petriNet.transitions.get(1).y)
					);
		}
	}

	@Nested
	@DisplayName("Places")
	class Places {
		@BeforeEach
		void beforeEach() {
			petriNet = new PetriNetModel();
			petriNet.addPlace("p1");
			petriNet.setPosition("p1", 3, 9000);
			petriNet.setName("p1", "mailbox");
			petriNet.addPlace("p2");
			petriNet.setPosition("p2", 5000, 3);
			petriNet.setName("p2", "post car");
			petriNet.setTokens("p2", 3);
		}

		@Test
		@DisplayName("are loaded")
		void correctAmount() {
			assertEquals(2, petriNet.places.size());
		}

		@Test
		@DisplayName("have the correct id")
		void correctId() {
			assertAll("ids",
					() -> assertEquals("p1", petriNet.places.get(0).id),
					() -> assertEquals("p2", petriNet.places.get(1).id)
					);
		}

		@Test
		@DisplayName("have the correct name")
		void correctName() {
			assertAll("names",
					() -> assertEquals("mailbox", petriNet.places.get(0).name),
					() -> assertEquals("post car", petriNet.places.get(1).name)
					);
		}

		@Test
		@DisplayName("have the correct tokens")
		void correctTokens() {
			assertAll("tokens",
					() -> assertEquals(0, petriNet.places.get(0).currentTokens),
					() -> assertEquals(0, petriNet.places.get(0).startTokens),
					() -> assertEquals(3, petriNet.places.get(1).currentTokens),
					() -> assertEquals(3, petriNet.places.get(1).startTokens)
					);
		}

		@Test
		@DisplayName("don't take negative token amounts")
		void noNegativeTokens() {
			petriNet.addPlace("p3");
			petriNet.setTokens("p3", -3);
			assertEquals(0, petriNet.places.get(2).currentTokens);
		}
	}

	@Nested
	@DisplayName("Arcs")
	class Arcs {
		@BeforeEach
		void beforeEach() {
			petriNet = new PetriNetModel();
			petriNet.addPlace("p1");
			petriNet.setPosition("p1", 3, 9000);
			petriNet.setName("p1", "mailbox");
			petriNet.setTokens("p1", 3);
			petriNet.addPlace("p2");
			petriNet.setPosition("p2", 5000, 3);
			petriNet.setName("p2", "post car");
			petriNet.addTransition("t1");
			petriNet.addArc("a1", "p1", "p2");
			petriNet.addArc("a2", "p2", "p1");
		}

		@Test
		@DisplayName("are loaded")
		void currentEqualStart() {
			assertEquals(2, petriNet.arcs.size());
		}

		@Test
		@DisplayName("have the correct id")
		void haveId() {
			assertAll("id",
					() -> assertEquals("a1", petriNet.arcs.get(0).id),
					() -> assertEquals("a2", petriNet.arcs.get(1).id)
					);
		}

		@Test
		@DisplayName("have the correct source")
		void haveSource() {
			assertAll("souce",
					() -> assertEquals("p1", petriNet.arcs.get(0).source.id),
					() -> assertEquals("p2", petriNet.arcs.get(1).source.id)
					);
		}

		@Test
		@DisplayName("have the correct target")
		void haveTarget() {
			assertAll("target",
					() -> assertEquals("p2", petriNet.arcs.get(0).target.id),
					() -> assertEquals("p1", petriNet.arcs.get(1).target.id)
					);
		}
	}

	@Nested
	@DisplayName("Firing a transition")
	class AfterFiring {
		@BeforeEach
		void beforeEach() {
			petriNet = new PetriNetModel();
			petriNet.addPlace("p1");
			petriNet.setPosition("p1", 3, 9000);
			petriNet.setName("p1", "place 1");
			petriNet.setTokens("p1", 3);
			petriNet.addPlace("p2");
			petriNet.setPosition("p2", 5000, 3);
			petriNet.setName("p2", "place 2");
			petriNet.addTransition("t1");
			petriNet.setName("t1", "t1 transition");
			petriNet.addTransition("t2");
			petriNet.setName("t2", "t2 transition");
			petriNet.addArc("a1", "p1", "t1");
			petriNet.addArc("a2", "t1", "p2");
			petriNet.addArc("a3", "p2", "t2");
			petriNet.addArc("a4", "t2", "p1");
		}

		@Test
		@DisplayName("transitions are enabled and disabled correctly")
		void enabledAndDisabled() {
			List<Transition> enabled = new ArrayList<>();
			for (Transition transition : petriNet.transitions) {
				if (transition.isEnabled) {
					enabled.add(transition);
				}
			}
			assertAll("enabled",
					() -> assertEquals(1, enabled.size()),
					() -> assertEquals("t1", enabled.get(0).id)
					);
		}

		@Test
		@DisplayName("input places have 1 token less")
		void inputs() {
			assertEquals(3, petriNet.places.get(0).currentTokens);
			petriNet.fire(petriNet.transitions.get(0));
			assertEquals(2, petriNet.places.get(0).currentTokens);
		}

		@Test
		@DisplayName("output places have 1 token more")
		void outputs() {
			assertEquals(0, petriNet.places.get(1).currentTokens);
			petriNet.fire(petriNet.transitions.get(0));
			assertEquals(1, petriNet.places.get(1).currentTokens);
		}

		@Test
		@DisplayName("sets isCustomMarking flag")
		void customMarkingFlag() {
			petriNet.fire(petriNet.transitions.get(0));
			assertEquals(true, petriNet.isAtCustomMarking);
		}

		@Test
		@DisplayName("stores last transition")
		void storesLastTransition() {
			petriNet.fire(petriNet.transitions.get(0));
			assertEquals("t1", petriNet.lastTransition.id);
		}
	}	

	@Test
	@DisplayName("resets to start token amount")
	void currentEqualStart() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setPosition("p1", 3, 9000);
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 3);
		petriNet.addPlace("p2");
		petriNet.setPosition("p2", 5000, 3);
		petriNet.setName("p2", "place 2");
		petriNet.addTransition("t1");
		petriNet.setName("t1", "t1 transition");
		petriNet.addTransition("t2");
		petriNet.setName("t2", "t2 transition");
		petriNet.addArc("a1", "p1", "t1");
		petriNet.addArc("a2", "t1", "p2");
		petriNet.addArc("a3", "p2", "t2");
		petriNet.addArc("a4", "t2", "p1");
		List<Integer> startTokens = new ArrayList<>();
		for (Place place : petriNet.places) {
			startTokens.add(place.startTokens);
		}
		petriNet.fire(petriNet.transitions.get(0));
		petriNet.reset();
		for (int i = 0; i < startTokens.size(); i ++) {
			if (startTokens.get(i) != petriNet.places.get(i).currentTokens) {
				fail(petriNet.places.get(i).id + " has the wrong amount" +
						" of tokens.");
			}
		}
	}

	@Test
	@DisplayName("stores selected place")
	void storesSelectedPlace() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.addPlace("p2");
		petriNet.setActivePlace("p2");
		assertEquals("p2", petriNet.selectedPlace.id);
	}

	@Test
	@DisplayName("removes selected place")
	void removesSelectedPlace() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.addPlace("p2");
		petriNet.setActivePlace("p2");
		petriNet.removeActivePlace();
		assertTrue(petriNet.selectedPlace == null);
	}

	@Test
	@DisplayName("increases tokens by 1 via addToken")
	void increasesTokens() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setPosition("p1", 3, 9000);
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 3);
		petriNet.setActivePlace("p1");
		petriNet.addUserToken();
		assertEquals(4, petriNet.places.get(0).currentTokens);
	}

	@Test
	@DisplayName("removes tokens by 1 via removeTokens")
	void decreasesTokens() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setPosition("p1", 3, 9000);
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 3);
		petriNet.setActivePlace("p1");
		petriNet.removeUserToken();
		assertEquals(2, petriNet.places.get(0).currentTokens);
	}

	@Test
	@DisplayName("doesn't remove tokens if result would be negative")
	void noNegativeTokens() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setPosition("p1", 3, 9000);
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 0);
		petriNet.setActivePlace("p1");
		petriNet.removeUserToken();
		assertEquals(0, petriNet.places.get(0).currentTokens);
	}

	@Test
	@DisplayName("removes isAtCustomMarking flag when user edits tokens")
	void customMarkingFlag() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setPosition("p1", 3, 9000);
		petriNet.setName("p1", "place 1");
		petriNet.setTokens("p1", 0);
		petriNet.setActivePlace("p1");
		petriNet.addUserToken();
		assertTrue(!petriNet.isAtCustomMarking);
	}

	@Test
	@DisplayName("sets file changed flag")
	void fileChangedFlag() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.setActivePlace("p1");
		petriNet.addUserToken();
		assertTrue(petriNet.fileChanged);
	}

	@Test
	@DisplayName("stores file name")
	void storesFileName() {
		FileSystemModel fs = new FileSystemModel();
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertEquals(
				"110-B1-N01-A00-EineStelleZweiMarken.pnml",
				petriNet.fileName);
	}

	@Test
	@DisplayName("returns the location from id")
	void returnsLocation() {
		petriNet = new PetriNetModel();
		petriNet.addPlace("p1");
		petriNet.addTransition("t2");
		assertAll("returns location",
				() -> assertEquals("p1", petriNet.getLocation("p1").id),
				() -> assertEquals("t2", petriNet.getLocation("t2").id)
				);
	}

	@Test
	@DisplayName("loads another petri net")
	void loads() {
		FileSystemModel fs = new FileSystemModel();
		File file1 = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet1 = fs.readNewFile(file1);

		File file2 = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet2 = fs.readNewFile(file2);

		petriNet1.load(petriNet2);

		assertAll("empty after init",
				() -> assertEquals(petriNet1.transitions, petriNet2.transitions),
				() -> assertEquals(petriNet1.places, petriNet2.places),
				() -> assertEquals(petriNet1.arcs, petriNet2.arcs),
				() -> assertEquals(
						petriNet1.selectedPlace,
						petriNet2.selectedPlace),
				() -> assertEquals(
						petriNet1.isAtCustomMarking,
						petriNet2.isAtCustomMarking),
				() -> assertEquals(petriNet1.fileChanged, petriNet2.fileChanged),
				() -> assertEquals(
						petriNet1.lastTransition,
						petriNet2.lastTransition),
				() -> assertEquals(petriNet1.fileName, petriNet2.fileName)
				);
	}
}
