package demo;

import java.io.File;
import java.util.TreeSet;

/**
 * Diese Klasse enthält die main Methode zum Starten des Demos.
 * 
 * @author ProPra-Team FernUni Hagen
 *
 */
public class ProPra_WS21_Demo {

	/**
	 * In der main-Methode wird die grafische Benutzeroberfläche des Programms
	 * gestartet.
	 * 
	 * @param args
	 *            wird nicht genutzt
	 */
	public static void main(String[] args) {
		
		// festlegen, dass der Swing Viewer verwendet werden soll 
		System.setProperty("org.graphstream.ui", "swing");
		
		// System Properties ausgegeben
		System.out.println("Alle System Properties");
		System.out.println("---------------------");
		System.out.println(getAllSystemProperties());
				
		System.out.println("Besonders wichtige System Properties");
		System.out.println("------------------------------------");
		System.out.println("user.dir     = " + System.getProperty("user.dir"));
		System.out.println("java.version = " + System.getProperty("java.version"));
		System.out.println("java.home    = " + System.getProperty("java.home"));
		System.out.println();
				
		// Prüfen, ob das Beispiel-Verzeichnis gefunden wird
		File bsp = new File("../ProPra-WS21-Basis/Beispiele");
		System.out.println("Überprüfen des Beispiel-Verzeichnisses");
		System.out.println("--------------------------------------");
		System.out.println("Verzeichnis mit Beispielen: " + bsp);
		System.out.println("Verzeichnis existiert?: " + bsp.exists());
		System.out.println();
				
		// Graph erzeugen
		MyGraph graph = new MyGraph();
		
		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MyFrame("ProPra-WS21-Demo", graph);
			}
		});
	}

	
	/**
	 * Liefert einen String zurück, der alle System Properties (und ihre aktuellen Werte) zeilenweise und in alphabetischer Reihenfolge enthält. 
	 * @return String mit allen System Properties in alphabetischer Reihenfolge.
	 */
	public static String getAllSystemProperties() {
		TreeSet<String> propSet = new TreeSet<String>();
		for (Object propName : System.getProperties().keySet())
		{
			propSet.add((String)propName + " = " + System.getProperty((String)propName));
		}
		String propertiesString = "";
		for (String prop : propSet)
		{
			propertiesString += prop + "\n";
		}
		return propertiesString;
	}

	
}
