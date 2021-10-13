package io.petermueller.petrinetz.models.filesystem;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.petermueller.petrinetz.models.petrinet.*;
import io.petermueller.petrinetz.controllers.EventListener;
import io.petermueller.petrinetz.controllers.Event;

public class FilesystemModel {
	private PetriNetModel petriNetModel;
	private List<EventListener> listeners = new ArrayList<EventListener>();
	public File prevFile;
	public File nextFile;
	private JFileChooser fc;
	
	public FilesystemModel(PetriNetModel petriNetModel) {
		this.petriNetModel = petriNetModel;
		fc = new JFileChooser(
			System.getProperty("user.dir") + "/../ProPra-WS21-Basis/Beispiele"
		);
		fc.setFileFilter(
			new FileNameExtensionFilter("Petri Net Markup Language", "pnml")
		);
	}
	
	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}

	public String getCurrentFile() {
		return fc.getSelectedFile().getName();
	}
	
	public void readNewFile(JFrame frame) {
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            petriNetModel.clear();
            PNMLParser pnmlParser = new PNMLParser(file, petriNetModel);
            pnmlParser.initParser();
			pnmlParser.parse();
			checkPrevAndNextFiles();
			for (EventListener listener : listeners) {
				listener.event(Event.NEW_FILE_OPENED);
			}
        }
	}
	
	public void readNewFile(File file) {
        fc.setSelectedFile(file);
        petriNetModel.clear();
        PNMLParser pnmlParser = new PNMLParser(file, petriNetModel);
        pnmlParser.initParser();
		pnmlParser.parse();
		checkPrevAndNextFiles();
		for (EventListener listener : listeners) {
			listener.event(Event.NEW_FILE_OPENED);
		}
	}
	
	public List<File> getAllFilesInDir() {
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
	
	public void checkPrevAndNextFiles() {
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
}