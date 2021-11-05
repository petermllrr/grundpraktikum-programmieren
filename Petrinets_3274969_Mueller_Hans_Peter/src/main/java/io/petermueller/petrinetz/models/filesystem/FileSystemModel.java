package io.petermueller.petrinetz.models.filesystem;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventEmitter;

public class FileSystemModel extends EventEmitter{
	public File prevFile;
	public File nextFile;
	private JFileChooser fc;

	public FileSystemModel() {
		fc = new JFileChooser(
				System.getProperty("user.dir") +
				"/../ProPra-WS21-Basis/Beispiele");
		fc.setFileFilter(
				new FileNameExtensionFilter(
						"Petri Net Markup Language", "pnml"));
	}

	public String getCurrentFileName() {
		return fc.getSelectedFile().getName();
	}

	public File getCurrentFile() {
		return fc.getSelectedFile();
	}

	public PetriNetModel readNewFile(JFrame frame) {
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!fileIsReadable()) {
				fireEvent(Event.FILESYSTEM_INVALID_FILE);
				return null;
			} else {
				checkPrevAndNextFiles();
				return processFile(file);
			}
		}
		return null;
	}

	public PetriNetModel readNewFile(File file) {
		if (!file.canRead()) {
			return null;
		} else {
			fc.setSelectedFile(file);
			checkPrevAndNextFiles();
			return processFile(file);
		}
	}

	public List<PetriNetModel> readNewFiles(JFrame frame) {
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			List<PetriNetModel> petriNets = new ArrayList<PetriNetModel>();
			for (File file : files) {
				if (!fileIsReadable()) {
					fireEvent(Event.FILESYSTEM_INVALID_FILE);
					return null;
				} else {
					petriNets.add(processFile(file));
				}
			}
			Collections.sort(petriNets, new Comparator<PetriNetModel>() {
				@Override
				public int compare(PetriNetModel o1, PetriNetModel o2) {
					return o1.fileName.compareTo(o2.fileName);
				}
			});
			if (petriNets.size() != 0) {
				return petriNets;
			}
		}
		return null;
	}

	public void notifyGui() {
		fireEvent(Event.FILESYSTEM_NEW_FILE_OPENED);
	}

	private List<File> getAllFilesInDir() {
		File dir = fc.getSelectedFile().getParentFile();
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".pnml");
			}
		});
		Arrays.sort(files);
		List<File> fileList = Arrays.asList(files);
		return fileList;
	}

	private void checkPrevAndNextFiles() {
		List<File> files = getAllFilesInDir();
		int index = files.indexOf(fc.getSelectedFile());
		if (index == 0) {
			prevFile = null;
		} else {
			prevFile = files.get(index - 1);
		}
		if (index == files.size() - 1) {
			nextFile = null;
		} else {
			nextFile = files.get(index + 1);
		}
	}

	private PetriNetModel processFile(File file) {
		PetriNetModel petriNet = new PetriNetModel();
		PNMLParser pnmlParser = new PNMLParser(file, petriNet);
		pnmlParser.initParser();
		pnmlParser.parse();
		petriNet.fileName = file.getName();
		return petriNet;
	}

	private boolean fileIsReadable() {
		List<File> pnmlFiles = getAllFilesInDir();
		File selectedFile = fc.getSelectedFile();
		if (!selectedFile.canRead() ||
				pnmlFiles.indexOf(selectedFile) == -1) {
			return false;
		} else {
			return true;
		}
	}
}