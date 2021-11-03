/**
 * 
 */
package tests;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.BoundednessAnalysis;

/**
 * @author petermueller
 *
 */
class BoundednessAnalysisTest {
	private PetriNetModel petriNet;

	@Nested
	@DisplayName("Bounded petri nets")
	class Bounded {
		private BoundednessAnalysis analysis;

		@BeforeEach
		void beforeEach() throws Exception {
			FileSystemModel fs = new FileSystemModel();
			File file = new File(System.getProperty(
					"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
					"110-B1-N01-A00-EineStelleZweiMarken.pnml");
			petriNet = fs.readNewFile(file);
			analysis = new BoundednessAnalysis(petriNet);
		}

		@Test
		@DisplayName("detects bounded petrinets")
		void detectsBounded() {
			assertTrue(analysis.isBounded);
		}

		@Test
		@DisplayName("Correct nodes")
		void correctNodes() {
			assertEquals(1, analysis.rGraph.markings.size());
		}

		@Test
		@DisplayName("Correct edges")
		void correctEdges() {
			assertEquals(0, analysis.rGraph.arcs.size());
		}
	}


	@Nested
	@DisplayName("Unbounded petri nets")
	class Unbounded {
		@BeforeEach
		void beforeEach() {
			FileSystemModel fs = new FileSystemModel();
			File file = new File(System.getProperty(
					"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
					"230-B0-P02-Qxx-Counter.pnml");
			petriNet = fs.readNewFile(file);
		}

		@Test
		@DisplayName("detects unbounded petri nets")
		void detectsunbounded() {
			BoundednessAnalysis analysis = new BoundednessAnalysis(petriNet);
			assertTrue(!analysis.isBounded);
		}

		@Test
		@DisplayName("correct m1")
		void m1() {

		}

		@Test
		@DisplayName("correct m2")
		void m2() {

		}
	}
}