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
import io.petermueller.petrinetz.models.petrinet.PetriNetModel;
import io.petermueller.petrinetz.util.Event;
import io.petermueller.petrinetz.util.EventEmitter;

/**
 * This model offers methods to read files from the user's local filesystem. It
 * manages the Swing FileChooser, opens file choosers so users can select files,
 * and passes selected filtes further on to the PNML parser. It also holds
 * information about the previous and next file in the current directory so
 * other components like the toolbar can access them.
 * 
 * @author Hans Peter Müller (3274969)
 * @see javax.swing.JFileChooser
 */
public class FileSystemModel extends EventEmitter{
	/**
	 * The previous PNML file in the directory, null if no previous file exists.
	 */
	public File prevFile;
	/**
	 * The next PNML file in the directory, null if no next file exists.
	 */
	public File nextFile;
	private JFileChooser fc;

	/**
	 * The public constructor creates a
	 * {@link javax.swing.JFileChooser JFileChooser}
	 * and sets the file filter to accept only PNML files.
	 */
	public FileSystemModel() {
		fc = new JFileChooser(
				System.getProperty("user.dir") +
				"/../ProPra-WS21-Basis/Beispiele");
		fc.setFileFilter(
				new FileNameExtensionFilter(
						"Petri Net Markup Language", "pnml"));
	}

	/**
	 * Returns the file name of the last read file.
	 * 
	 * @return the file name
	 */
	public String getCurrentFileName() {
		return fc.getSelectedFile().getName();
	}

	/**
	 * Returns a reference to the last read file.
	 * 
	 * @return a reference to the file
	 */
	public File getCurrentFile() {
		return fc.getSelectedFile();
	}

	/**
	 * Creates a file choosing dialog, reads the file selected by the user,
	 * parses it, and returns a {@link PetriNetModel}. If the file can't be
	 * read, a {@code FILESYSTEM_INVALID_FILE} event is fired and null is
	 * returned.
	 * 
	 * @param frame the {@link JFrame} from which the file chooser should be
	 * started
	 * @return a {@link PetriNetModel} or null if the file can't be read
	 */
	public PetriNetModel readNewFile(JFrame frame) {
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!fileIsReadable(file)) {
				fireEvent(Event.FILESYSTEM_INVALID_FILE);
				return null;
			} else {
				checkPrevAndNextFiles();
				return processFile(file);
			}
		}
		return null;
	}
	
	/**
	 * Reads a single file referenced, passes it to the parser and returns a 
	 * {@link PetriNetModel}.
	 * 
	 * @param file the file to read
	 * @return a {@link PetriNetModel} or null if the file can't be read
	 */
	public PetriNetModel readNewFile(File file) {
		fc.setSelectedFile(file);
		if (!fileIsReadable(file)) {
			fireEvent(Event.FILESYSTEM_INVALID_FILE);
			return null;
		} else {
			checkPrevAndNextFiles();
			return processFile(file);
		}
	}

	/**
	 * Opens a file chooser to select multiple PNML files, reads all files and
	 * returns a list of {@link PetriNetModel PetrinetModels}. If some files
	 * can't be read, it fires a {@code FILESYSTEM_INVALID_FILE} event.
	 * 
	 * @param frame the {@link JFrame} from which the file chooser should be
	 * started
	 * @return a list of {@link PetriNetModel PetriNetModels}, the list is empty
	 * if no file could be read
	 */
	public List<PetriNetModel> readNewFiles(JFrame frame) {
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			List<PetriNetModel> petriNets = new ArrayList<PetriNetModel>();
			Boolean containsUnreadableFile = false;
			for (File file : files) {
				if (!fileIsReadable(file)) {
					containsUnreadableFile = true;
				} else {
					petriNets.add(processFile(file));
				}
			}
			if (containsUnreadableFile) {
				fireEvent(Event.FILESYSTEM_INVALID_FILE);
			}
			/**
			 * Sorts the read Petri net files alphabetically.
			 */
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

	/**
	 * Fires an event to notify all listening views.
	 * 
	 * @see Event
	 */
	public void notifyGui() {
		fireEvent(Event.FILESYSTEM_NEW_FILE_OPENED);
	}

	/**
	 * Reads all readable PNML files in a directory.
	 * 
	 * @return a list of {@link File Files}
	 */
	private List<File> getAllFilesInDir() {
		File dir = fc.getSelectedFile().getParentFile();
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".pnml");
			}
		});
		if (files != null) {
			Arrays.sort(files);
			List<File> fileList = Arrays.asList(files);
			return fileList;
		} else {
			return null;
		}
	}

	/**
	 * Checks if the currrently selected PNML file has a previous or next file
	 * and assigns references to the class attributes {@code nextFile} and
	 * {@code prevFile}.
	 */
	private void checkPrevAndNextFiles() {
		List<File> files = getAllFilesInDir();
		int index = files.indexOf(fc.getSelectedFile());
		/**
		 * Case if the read file is the first in the directory and no
		 * previous file is present.
		 */
		if (index == 0) {
			prevFile = null;
		} else {
			prevFile = files.get(index - 1);
		}
		/**
		 * Case if the read file is the last in the directory and no next file
		 * is present.
		 */
		if (index == files.size() - 1) {
			nextFile = null;
		} else {
			nextFile = files.get(index + 1);
		}
	}

	/**
	 * Passes the read file to the PNML parser.
	 * 
	 * @param file the file to parse
	 * @return the {@link PetriNetModel} from the file
	 */
	private PetriNetModel processFile(File file) {
		PetriNetModel petriNet = new PetriNetModel();
		PNMLParser pnmlParser = new PNMLParser(file, petriNet);
		pnmlParser.initParser();
		pnmlParser.parse();
		petriNet.fileName = file.getName();
		return petriNet;
	}

	/**
	 * Checks if a file is readable and a PNML file. To do so, it compares
	 * the file name with a list of all PNML files in the current directory.
	 * 
	 * @param file the file to check
	 * @return true if the file is readable, false if it's not readable or not
	 * a PNML file
	 */
	private boolean fileIsReadable(File file) {
		List<File> pnmlFiles = getAllFilesInDir();
		if (!file.canRead() ||
				pnmlFiles.indexOf(file) == -1) {
			return false;
		} else {
			return true;
		}
	}
}