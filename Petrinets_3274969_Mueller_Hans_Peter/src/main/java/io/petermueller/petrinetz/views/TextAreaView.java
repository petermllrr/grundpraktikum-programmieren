package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;
import io.petermueller.petrinetz.util.BatchProcessing;
import io.petermueller.petrinetz.util.BoundednessAnalysis;

/**
 * Renders the text area of the app's main view, offers methods to print
 * text messages in it and provides a popup menu with additional
 * functionality.
 * 
 * @author Hans Peter Müller (3274969)
 */
public class TextAreaView {
	/**
	 * The main text area component.
	 */
	public JScrollPane textArea;
	/**
	 * The text field that holds the text content.
	 */
	public JTextArea inputField;
	/**
	 * The popup menu.
	 */
	public PopupMenu popupMenuView;

	/**
	 * Initializes the component, renders the UI and creates the popup menu.
	 */
	public TextAreaView() {
		renderUI();
		popupMenuView = new PopupMenu();
	}

	/**
	 * Outputs a text string.
	 * @param text the text to output
	 */
	public void print(String text) {
		printTextBelow(text);
		scrollToEnd();
	}

	/**
	 * Clears the text content.
	 */
	public void clear() {
		inputField.setText("");
	}

	/**
	 * Prints the result of a single boundedness analysis.
	 * 
	 * @param analysis the analysis to print
	 */
	public void printBoundednessResults(BoundednessAnalysis analysis) {
		String output = "";
		if (!lastLineIsEmpty()) {
			output = output + "\n";
		}
		output = output + "Beschränktheitsanalyse:\n" +
				analysis.petriNet.fileName + " ";
		if (analysis.isBounded) {
			int possibleMarkings = analysis.rGraph.markings.size();
			output = output +
					"ist beschränkt.\n" +
					possibleMarkings + " mögliche Markierungen.";
		} else {
			output = output + 
					"ist unbeschränkt. Abbruchkriterien:\n" +
					"m    " + analysis.m1.id + "\n" +
					"m'   " + analysis.m2.id + "\n";
			boolean firstLine = true;
			for (TransitionArc arc : analysis.terminationPath) {
				if (firstLine) {
					output = output +
							"Pfad " + arc.id;
					firstLine = false;
				} else {
					output = output + "\n" +
							"     " +
							arc.id;
				}
			}
		}
		print(output + "\n");
	}

	/**
	 * Prints the results of a boundedness analysis of multiple files in a
	 * table.
	 * 
	 * @param batch the analyzed files
	 */
	public void printBatchResults(BatchProcessing batch) {
		String headerName = "Dateiname";
		String headerBounded = "beschränkt";
		String headerDetailsLine1 = "Knoten / Kanten bzw.";
		String headerDetailsLine2 = "Pfadlänge:Pfad; m, m'";
		int lengthName = getMaxFileNameLength(batch);
		int lengthBounded = headerBounded.length();
		int lengthDetailsNodes = getMaxDetailsNodesLength(
				batch,
				headerDetailsLine2);
		int lengthDetailsM1 = getLengthDetailM1Length(batch);
		int lengthDetailsM2 = getLengthDetailM2Length(batch);

		String output = "";
		if (!lastLineIsEmpty()) {
			output = output + "\n";
		}
		output = output + generateTableHeader(
				lengthName, lengthBounded, lengthDetailsNodes,
				lengthDetailsM1, lengthDetailsM2, headerName,
				headerBounded, headerDetailsLine1, headerDetailsLine2);

		output = output + generateHorizontalLine(
				lengthName,
				lengthBounded,
				lengthDetailsNodes,
				lengthDetailsM1,
				lengthDetailsM2);

		for (BoundednessAnalysis analysis : batch.analysisList) {
			output = output + generateAnalysisLine(
					analysis, lengthName, lengthBounded, lengthDetailsNodes,
					lengthDetailsM1, lengthDetailsM2);
		}		
		print(output + "\n");
	}

	/**
	 * Cuts out the currently selected text from the input field.
	 */
	public void cutText() {
		inputField.replaceSelection("");
	}

	/**
	 * Renders the UI.
	 */
	private void renderUI() {
		inputField = new JTextArea();
		textArea = new JScrollPane(inputField);
		textArea.setBorder(
				BorderFactory.createLineBorder(new Color(0xDADCE0), 1)
				);
		inputField.setText("PNML Datei via \"Datei > Öffnen…\" auswählen\n");
		inputField.setBackground(Color.white);
		inputField.setBorder(new EmptyBorder(8, 8, 8, 8));
		inputField.setFont(new Font("monospaced", Font.PLAIN, 13));
		inputField.setWrapStyleWord(true);
		inputField.setLineWrap(true);
		inputField.setEditable(false);
	}

	/**
	 * Scrolls the text field to the end.
	 */
	private void scrollToEnd() {
		inputField.setCaretPosition(inputField.getDocument().getLength());
	}

	/**
	 * Attaches a string to the text fields content.
	 * 
	 * @param newText the text to attach
	 */
	private void printTextBelow(String newText) {
		String text = inputField.getText();
		text = text + newText;
		inputField.setText(text);
	}

	/**
	 * Adds a row to the tabular output of the boundedness analysis.
	 * 
	 * @param analysis           the analysis
	 * @param lengthName         the maximum length of the filename column
	 * @param lengthBounded      the maximum length of the boundedness column
	 * @param lengthDetailsNodes the maximum length of the details column
	 * @param lengthDetailsM1    the maximum length of the m1 marking string
	 * @param lengthDetailsM2    the maximum length of the m2 marking string
	 * @return                   the formatted table header
	 */
	private String generateAnalysisLine(
			BoundednessAnalysis analysis,
			int lengthName, int lengthBounded, int lengthDetailsNodes,
			int lengthDetailsM1, int lengthDetailsM2) {
		String filename = analysis.petriNet.fileName;
		if (analysis.isBounded) {
			String bounded = "ja";
			String nodes = Integer.toString(analysis.rGraph.markings.size());
			String edges = Integer.toString(analysis.rGraph.arcs.size());
			return String.format(
					"%1$-" + lengthName +
					"s | %2$-" + lengthBounded +
					"s | %3$2s / %4$2s \n",
					filename, bounded, nodes, edges);
		} else {
			String bounded = "nein";
			String path = generatePathString(analysis);
			String m1 = analysis.m1.id;
			String m2 = analysis.m2.id;
			return String.format(
					"%1$-" + lengthName +
					"s | %2$-" + lengthBounded +
					"s | %3$-" + lengthDetailsNodes +
					"s %4$-" + lengthDetailsM1 +
					"s %5$-" + lengthDetailsM2 +
					"s \n",
					filename, bounded, path, m1, m2);
		}
	}

	/**
	 * Adds a horizontal line to the tabular output of the boundedness analysis
	 * seperating the table header from the table body.
	 * 
	 * @param lengthName         the maximum length of the filename column
	 * @param lengthBounded      the maximum length of the boundedness column
	 * @param lengthDetailsNodes the maximum length of the details column
	 * @param lengthDetailsM1    the maximum length of the m1 marking string
	 * @param lengthDetailsM2    the maximum length of the m2 marking string
	 * @return                   the formatted table line
	 */
	private String generateHorizontalLine(
			int lengthName, 
			int lengthBounded, 
			int lengthDetailsNodes, 
			int lengthDetailsM1,
			int lengthDetailsM2) {
		String line;
		line = new String(new char[lengthName + 1]).replace("\0", "-");
		line = line + "|";
		line = line + 
				new String(new char[lengthBounded + 2]).replace("\0", "-");
		line = line + "|";
		int detailsLenght = lengthDetailsNodes +
				lengthDetailsM1 +
				lengthDetailsM2;
		line = line + 
				new String(new char[detailsLenght + 6]).replace("\0", "-");
		line = line + "\n";
		return line;
	}

	/**
	 * Calculates the maximum filename length of all files from a
	 * boundedness analysis.
	 * 
	 * @param batch the batch processed files
	 * @return      the maximum filename length
	 */
	private int getMaxFileNameLength(BatchProcessing batch) {
		int maxFileNameLength = 0;
		for (BoundednessAnalysis analysis : batch.analysisList) {
			if (analysis.petriNet.fileName.length() > maxFileNameLength) {
				maxFileNameLength = analysis.petriNet.fileName.length();
			}
		}
		return maxFileNameLength;
	}

	/**
	 * Generates the detection path string.
	 * 
	 * @param analysis the boundedness analysis
	 * @return         the detection path in string format
	 */
	private String generatePathString(BoundednessAnalysis analysis) {
		if (analysis.isBounded) {
			return "";
		} else {
			int length = analysis.terminationPath.size();
			String nodes = "(";
			for (TransitionArc arc : analysis.terminationPath) {
				nodes = nodes + arc.shortId + ",";
			}
			nodes = removeLastCharacter(nodes);
			nodes = nodes + ")";
			return length + ":" + nodes + ";";
		}
	}

	/**
	 * Removes the last character from a string.
	 * 
	 * @param string input string
	 * @return       string minus the last character
	 */
	private String removeLastCharacter(String string) {
		return string.substring(0, string.length() - 1);
	}

	/**
	 * Calculates the maximum length of the details column for the tabular
	 * output.
	 * 
	 * @param batch  the batch processed files
	 * @param header the table header
	 * @return       the maximum length of the details column
	 */
	private int getMaxDetailsNodesLength(
			BatchProcessing batch,
			String header) {
		int maxLength = 0;
		for (BoundednessAnalysis analysis : batch.analysisList) {
			int stringLength = generatePathString(analysis).length();
			if (stringLength > maxLength) {
				maxLength = stringLength;
			}
		}
		if (header.length() > maxLength) {
			maxLength = header.length();
		}
		return maxLength;
	}

	/**
	 * Calculates the length of the longest m marking sting.
	 * 
	 * @param batch the batch processed files
	 * @return      the maximum length of all m marking strings
	 */
	private int getLengthDetailM1Length(BatchProcessing batch) {
		int maxLength = 0;
		for (BoundednessAnalysis analysis : batch.analysisList) {
			if(!analysis.isBounded) {
				int stringLength = analysis.m1.id.length();
				if (stringLength > maxLength) {
					maxLength = stringLength;
				}
			}
		}
		return maxLength;
	}

	/**
	 * Calculates the length of the longest m' marking string.
	 * 
	 * @param batch the batch processed files
	 * @return      the length of the longest m' marking string
	 */
	private int getLengthDetailM2Length(BatchProcessing batch) {
		int maxLength = 0;
		for (BoundednessAnalysis analysis : batch.analysisList) {
			if(!analysis.isBounded) {
				int stringLength = analysis.m2.id.length();
				if (stringLength > maxLength) {
					maxLength = stringLength;
				}
			}
		}
		return maxLength;
	}

	/**
	 * Generates and formats the table header for the boundedness analysis.
	 * 
	 * @param lengthName         the maximum length of the filename column
	 * @param lengthBounded      the maximum length of the boundedness column
	 * @param lengthDetailsNodes the maximum length of the details column
	 * @param lengthDetailsM1    the maximum length of the m1 marking string
	 * @param lengthDetailsM2    the maximum length of the m2 marking string
	 * @param headerName         table header for the name column
	 * @param headerBounded      table header for the boundedness column
	 * @param headerDetailsLine1 first line of the table header for the details
	 *                           column
	 * @param headerDetailsLine2 second line of the table header for the details
	 *                           column
	 * @return                   the formatted table header string
	 */
	private String generateTableHeader(
			int lengthName,
			int lengthBounded,
			int lengthDetailsNodes,
			int lengthDetailsM1,
			int lengthDetailsM2,
			String headerName,
			String headerBounded,
			String headerDetailsLine1,
			String headerDetailsLine2) {
		int detailsLength;
		if (lengthDetailsNodes + lengthDetailsM1 + lengthDetailsM2 == 0) {
			detailsLength = 100;
		} else {
			detailsLength = lengthDetailsNodes + lengthDetailsM1 + 
					lengthDetailsM2;
		}
		return String.format(
				"%5$" + lengthName +
				"s | %5$" + lengthBounded +
				"s | %3$-" + detailsLength + "s \n" +
				"%1$-" + lengthName +
				"s | %2$-" + lengthBounded +
				"s | %4$-" + detailsLength + "s \n",
				headerName,
				headerBounded,
				headerDetailsLine1,
				headerDetailsLine2,
				"");
	}

	/**
	 * Checks if the last line in the text field content is an empty line.
	 * 
	 * @return {@code true} if the last line is an empty line, {@code false}
	 *         otherwise
	 */
	private boolean lastLineIsEmpty() {
		String text = inputField.getText();
		String last2Characters = text.substring(Math.max(text.length() - 2, 0));
		if (last2Characters.equals("\n\n")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Popup menu for the text area. It offers additional functionality to
	 * copy and cut text.
	 * 
	 * @author Hans Peter Müller (3274969)
	 */
	public class PopupMenu {
		/**
		 * The popup menu component.
		 */
		public JPopupMenu popup;
		/**
		 * The "Kopieren" menu entry.
		 */
		public JMenuItem menuCopy;
		/**
		 * The "Ausschneiden" menu entry.
		 */
		public JMenuItem menuCut;
		/**
		 * The "Textbereich löschen" menu entry.
		 */
		public JMenuItem menuClear;

		/**
		 * Initializes the component.
		 */
		public PopupMenu() {
			renderUI();
		}

		/**
		 * Renders the UI.
		 */
		private void renderUI() {
			popup = new JPopupMenu();
			menuCopy = new JMenuItem("Kopieren");
			menuCut = new JMenuItem("Ausschneiden");	
			menuClear = new JMenuItem("Textbereich löschen");
			popup.add(menuCopy);
			popup.add(menuCut);
			popup.addSeparator();
			popup.add(menuClear);
		}
	}
}