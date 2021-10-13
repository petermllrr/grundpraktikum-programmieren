package junit_example;
/**
 * Ein einfacher "Taschenrechner", der über einen Speicher für das zuletzt berechnete Ergebnis verfügt.
 * Diese Klasse und ihre Methoden dienen der beispielhaften Illustration von JUnit Tests.
 */
public class Calculator {

	/**
	 * zuletzt berechnetes Ergebnis
	 */
	int lastResult = 0;

	/**
	 * Erzeugt eine neue Instanz eines Taschenrechners. 
	 */
	public Calculator() {
	}
	
    /** 
     * Addiert zwei ganze Zahlen.
     * @param a linker Summand
     * @param b rechter Summand
     * @return Summe der Zahlen
     */
    public int add(int a, int b) {
    	lastResult = a + b;
        return lastResult;
    }
    
    /**
     * Multipliziert zwei ganze Zahlen.
     * @param a linker Faktor
     * @param b rechter Faktor
     * @return Produkt der Zahlen
     */
    public int multiply(int a, int b) {
    	lastResult = a * b;
        return lastResult;

    }

    /**
     * Dividiert zwei ganze Zahlen und liefert nur den Ganzzahlquotient (ohne den Rest) zurück.
     * @param a	Divident
     * @param b Divisor
     * @return Ganzzahlquotient (ohne Rest)
     */
    public double divide(int a, int b) {
    	lastResult = a / b; 
        return lastResult;
    }
    
    /**
     * Setzt das zuletzt berechnete Ergebnis auf 0 zurück. 
     */
    public void reset() {
    	lastResult = 0;
    }
    
    /**
     * Liefert das zuletzt berechnete Ergebnis zurück.
     * @return zuletzt berechnetes Ergebnis
     */
    public int getLastResult() {
		return lastResult;
	}
    
}
