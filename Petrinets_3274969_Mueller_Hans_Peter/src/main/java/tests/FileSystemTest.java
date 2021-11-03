package tests;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.*;
import io.petermueller.petrinetz.models.filesystem.FileSystemModel;
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.models.petrinet.Place;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventListener;

/**
 * 
 */

/**
 * @author petermueller
 *
 */
@DisplayName("File System Model")
class FileSystemTest {

	private FileSystemModel fs;

	@BeforeEach
	void setUp() throws Exception {
		fs = new FileSystemModel();
	}

	@Test
	@DisplayName("loads 1 place")
	void loadPlace() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.places.size() == 1);
	}

	@Test
	@DisplayName("loads a place with id \"p1\"")
	void loadPlaceId() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.places.get(0).id.equals("p1"));
	}

	@Test
	@DisplayName("loads a place with name \"p1\"")
	void loadPlaceName() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.places.get(0).name.equals("p1"));
	}

	@Test
	@DisplayName("loads 2 markers")
	void loadMarkers() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		int markers = 0;
		for (Place place : petriNet.places) {
			markers = markers + place.currentTokens;
		}
		assertTrue(markers == 2);
	}

	@Test
	@DisplayName("loads 1 transition")
	void loadTransition() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.transitions.size() == 1);
	}

	@Test
	@DisplayName("loads a transition with id \"t1\"")
	void loadTransitionId() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.transitions.get(0).id.equals("t1"));
	}

	@Test
	@DisplayName("loads a transition with name \"t1\"")
	void loadTransitionName() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.transitions.get(0).name.equals("t1"));
	}

	@Test
	@DisplayName("loads 1 arc")
	void loadArcs() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.arcs.size() == 1);
	}

	@Test
	@DisplayName("load an arc with name \"a1\"")
	void loadArcName() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.arcs.get(0).id.equals("a1"));
	}

	@Test
	@DisplayName("returns null if file can't be loaded")
	void returnsNull() {
		File file = new File("does/not/exist");
		assertTrue(fs.readNewFile(file) == null);
	}

	@Test
	@DisplayName("generates correct file name")
	void generatesFileName() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		PetriNetModel petriNet = fs.readNewFile(file);
		assertTrue(petriNet.fileName.equals(
				"111-B1-N01-A00-EineStelleEineTransition.pnml"
				));
	}

	@Test
	@DisplayName("finds correct previous file in directory")
	void findsPrevFile() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		fs.readNewFile(file);
		assertTrue(fs.prevFile.getName().equals(
				"110-B1-N01-A00-EineStelleZweiMarken.pnml"
				));
	}

	@Test
	@DisplayName("sets prevFile to null if no previous file exists in " +
			"directory")
	void noPrevFile() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"110-B1-N01-A00-EineStelleZweiMarken.pnml");
		fs.readNewFile(file);
		assertTrue(fs.prevFile == null);
	}

	@Test
	@DisplayName("finds next file in directory")
	void findsNextFile() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		fs.readNewFile(file);
		assertTrue(fs.nextFile.getName().equals(
				"112-B1-N02-A01-EineStelleEineMarkeEineTransition.pnml"
				));
	}

	@Test
	@DisplayName("sets nextFile to null if no next file exists in directory")
	void noNextvFile() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"276-B0-P04-Q10-mehrere-Pfade.pnml");
		fs.readNewFile(file);
		assertTrue(fs.nextFile == null);
	}

	@Test
	@DisplayName("gets current file name")
	void currentFileName() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		fs.readNewFile(file);
		assertTrue(fs.getCurrentFileName().equals(
				"111-B1-N01-A00-EineStelleEineTransition.pnml"
				));
	}

	@Test
	@DisplayName("gets current file")
	void currentFile() {
		File file = new File(System.getProperty(
				"user.dir") + "/../ProPra-WS21-Basis/Beispiele/" +
				"111-B1-N01-A00-EineStelleEineTransition.pnml");
		fs.readNewFile(file);
		assertTrue(fs.getCurrentFile().getClass() == File.class);
	}

	@Test
	@DisplayName("sends a notification event")
	void notificationEvent() {
		fs.addEventListener(new EventListener() {
			@Override
			public void event(Event eventType) {
				if (eventType == Event.FILESYSTEM_NEW_FILE_OPENED) {
					assertTrue(true);
				}
			}
		});
		fs.notifyGui();
	}
}
