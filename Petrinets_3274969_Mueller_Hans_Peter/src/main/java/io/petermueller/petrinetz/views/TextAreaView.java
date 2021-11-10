package io.petermueller.petrinetz.views;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import io.petermueller.petrinetz.models.rgraph.TransitionArc;
import io.petermueller.petrinetz.util.BatchProcessing;
import io.petermueller.petrinetz.util.BoundednessAnalysis;

public class TextAreaView {
	public JScrollPane textArea;
	public JTextArea inputField;
	public PopupMenu popupMenuView;

	public TextAreaView() {
		renderUI();
		popupMenuView = new PopupMenu();
	}

	public void print(String text) {
		printTextBelow(text);
		scrollToEnd();
	}

	public void clear() {
		inputField.setText("");
	}

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
			for (TransitionArc arc : analysis.detectionPath) {
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

	public void cutText(String selected) {
		String currentText = inputField.getText();
		String newText = currentText.replace(selected, "");
		inputField.setText(newText);
		scrollToEnd();
	}

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
		inputField.setEditable(true);
	}

	private void scrollToEnd() {
		inputField.setCaretPosition(inputField.getDocument().getLength());
	}

	private void printTextBelow(String newText) {
		String text = inputField.getText();
		text = text + newText;
		inputField.setText(text);
	}

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

	private int getMaxFileNameLength(BatchProcessing batch) {
		int maxFileNameLength = 0;
		for (BoundednessAnalysis analysis : batch.analysisList) {
			if (analysis.petriNet.fileName.length() > maxFileNameLength) {
				maxFileNameLength = analysis.petriNet.fileName.length();
			}
		}
		return maxFileNameLength;
	}

	private String generatePathString(BoundednessAnalysis analysis) {
		if (analysis.isBounded) {
			return "";
		} else {
			int length = analysis.detectionPath.size();
			String nodes = "(";
			for (TransitionArc arc : analysis.detectionPath) {
				nodes = nodes + arc.shortId + ",";
			}
			nodes = removeLastCharacter(nodes);
			nodes = nodes + ")";
			return length + ":" + nodes + ";";
		}
	}

	private String removeLastCharacter(String string) {
		return string.substring(0, string.length() - 1);
	}

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

	private boolean lastLineIsEmpty() {
		String text = inputField.getText();
		String last2Characters = text.substring(Math.max(text.length() - 2, 0));
		if (last2Characters.equals("\n\n")) {
			return true;
		} else {
			return false;
		}
	}

	public class PopupMenu {
		public JPopupMenu popup;
		public JMenuItem menuCopy;
		public JMenuItem menuCut;

		public PopupMenu() {
			renderUI();
		}

		private void renderUI() {
			popup = new JPopupMenu();
			menuCopy = new JMenuItem("Kopieren");
			menuCut = new JMenuItem("Ausschneiden");	
			popup.add(menuCopy);
			popup.add(menuCut);
		}
	}
}