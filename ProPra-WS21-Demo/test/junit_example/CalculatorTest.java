package junit_example;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;


// Reihenfolge der Testausführung festlegen (ansonsten unbestimmt) 
// alternativ z.B. auch: @TestMethodOrder(MethodOrderer.MethodName.class)
@TestMethodOrder(MethodOrderer.DisplayName.class) 
@SuppressWarnings("javadoc")
class CalculatorTest {

	public static boolean INTENSIV_TESTING = false;
	
	private Calculator calc;
	
	
	
	// *** Vor- und _Nachbereitung für Tests ***
	// -----------------------------------------
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		System.out.println("Vorbereitungen für alle Tests treffen");
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		System.out.println("Aufräumarbeiten nach dem Testen durchführen");
	}

	@BeforeEach
	void setUp() throws Exception {
		System.out.println("neuen Rechner erzeugen");
		calc = new Calculator();
	}

	@AfterEach
	void tearDown() throws Exception {
		// hier gibt es nach einem Test nichts zu tun
	}

	
	
	// *** konkrete Tests ***
	// ----------------------
	
	@Test
	@DisplayName("Addition positiver Zahlen")
	void testAddSimple() {
		assertEquals(42, calc.add(20, 22));
	}

	
	@Test
	@DisplayName("Addition einer negativen Zahl")
	void testAddNegtaive() {
		assertEquals(8, calc.add(10, -2));
	}

	
	@Test
	@DisplayName("Addition von 0")
	void testAddZero() {
		assertEquals(10, calc.add(10, 0));
	}

	
	@Test
	@DisplayName("Division durch 0 mit erwarteter Exception")
	void testDivideByZeroWithException() {
		assertThrows(java.lang.ArithmeticException.class, () -> {
			calc.divide(77, 0);
		});
	}

	
	@Test
	@DisplayName("lastResult: Wert")
	void testLastResult() {
		calc.add(10, 8);
		assertEquals(18, calc.getLastResult());
	}
	
	
	@Test
	@DisplayName("lastResult: Verwendung")
	void testUseLastResult() {
		calc.add(50, 10);
		assertEquals(66, calc.add(calc.getLastResult(), 6));
	}	
	
	
	@Test
	@DisplayName("lastResult: Reset")
	void testReset() {
		calc.add(3, 4);
		calc.reset();
		assertEquals(0, calc.getLastResult());
	}

	
	// Beispiel für einen deaktivierten Test
	@Disabled("Test ist zu einfach.")
	@Test
	@DisplayName("Multiplikation zweier Zahlen")
	void testMultiplySimple() {
		assertEquals(36, calc.multiply(6, 6));
	}


	// Beispiel für einen Test, der nur dann durchgeführt werden soll,
	// wenn die angegebene Bedingung gilt (assumeTrue), 
	// ansonsten wird der Test ohne Ergebnis beendet
	// (d.h. eine nicht erfülltes assume... zählt nicht als Failure).
	@Test
	@DisplayName("Addition zweier negativer Zahlen")
	void testAddTwoNegtaives() {
		assumeTrue(INTENSIV_TESTING, "intensives Testen ist nicht aktiviert");	
		assertEquals(-120, calc.add(-100, -20));
	}

	
	// Beispiel für einen Test, der fehlschlägt 
	// (aufgeführt als blauer "Failure" in der JUnit View von Eclipse).
	// D.h. der fehlgeschlagene Test weist auf ein Problem hin.  
	@Test
	@DisplayName("Addition großer Zahlen")
	void testAddLarge() {
		assertEquals(((long)Integer.MAX_VALUE)+1, calc.add(Integer.MAX_VALUE, 1), "Integer Überlauf wird nicht abgefangen");
	}

	
	// Beispiel für einen Test, der fehlschlägt 
	// (aufgeführt als blauer "Failure" in der JUnit View von Eclipse).
	// D.h. der fehlgeschlagene Test weist auf ein Problem hin.  
	@Test
	@DisplayName("Division mit nicht-ganzzahligem Ergebnis")
	void testDivide() {
		assertEquals(4.5, calc.divide(9, 2));
	}
	
	
	// Beispiel für einen Test, während dessen Ausführung eine Exception auftritt
	// (aufgeführt als roter "Error" in der JUnit View von Eclipse).
	// D.h. der Test selbst enthält einen Fehler, der zunächst einmal beseitigt werden müsste
	// Der Test ist also quasi "falsch" programmiert (denn 8 geteilt durch 0 ergibt nicht 0, sondern eine Exception).  
	// Erst wenn der Test "korrekt" programmiert ist, kann festgestellt werden,
	// ob der Test erfolgreich ist oder fehlschlägt.
	@Test
	@DisplayName("Mehrere Operationen")
	void testMultipleOperations() {
		calc.add(3, 7);
		calc.multiply(calc.getLastResult(), 2);
		calc.add(-20, calc.getLastResult());
		assertEquals(0, calc.divide(8, calc.getLastResult()) );
	}

}
