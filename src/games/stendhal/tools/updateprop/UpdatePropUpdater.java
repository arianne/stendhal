package games.stendhal.tools.updateprop;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Properties;

/**
 * Updates update.properties for a new release.
 *
 * @author hendrik
 */
public class UpdatePropUpdater {
	private static final String NON_STENDHAL_FILES = "log4j.jar,jorbis.jar,marauroa-3.8.2.jar";

	private String oldFile;
	private String newFile;
	private String oldVersion;
	private String newVersion;
	private String folder;
	private String legacy;
	private Properties prop;
	
	/**
	 * Creates a new UpdatePropUpdater.
	 *
	 * @param oldFile    name of old file
	 * @param newFile    name of new file
	 * @param oldVersion last version
	 * @param newVersion new version
	 * @param folder     folder with files
	 * @param legacy     legacy prefix, may be empty
	 */
	public UpdatePropUpdater(final String oldFile, final String newFile, final String oldVersion, final String newVersion, String folder, String legacy) {
		this.newFile = newFile;
		this.newVersion = newVersion;
		this.oldFile = oldFile;
		this.oldVersion = oldVersion;
		this.folder = folder;
		this.legacy = legacy;
	}

	/**
	 * Updates the update.properties file.
	 *
	 * @throws IOException in case of an input/output error
	 */
	public void process() throws IOException {
		loadOldUpdateProperties();
		updateVersion();
		updateInit();
		updateUpdateFileList();
		updateFileSize();
		writeNewUpdateProperties();
	}

	/**
	 * loads the current version of the update.properties
	 *
	 * @throws IOException in case of an input/output error
	 */
	private void loadOldUpdateProperties() throws IOException {
		prop = new Properties();
		InputStream is;
		if (oldFile.indexOf(":") > 2) {
			URL url = new URL(oldFile);
			is = url.openStream();
		} else {
			is = new FileInputStream(oldFile);
		}
		prop.load(is);
		is.close();
	}

	/**
	 * updates the version number.
	 */
	private void updateVersion() {
		prop.put("version." + oldVersion, "UPDATE_NEEDED");
		prop.put("version." + newVersion, "CURRENT");
		prop.put("version.destination", newVersion);
	}

	/**
	 * updates the init statement
	 */
	private void updateInit() {
		// TODO: generate automatically instead of hardcoding it.
		prop.put("init.file-list", NON_STENDHAL_FILES + ",stendhal-data-" + newVersion + ".jar,stendhal-" + newVersion + ".jar");
		prop.put("init.version", newVersion);

	}

	/**
	 * updates the update-file-list
	 */
	private void updateUpdateFileList() {
		// TODO: generate automatically instead of hardcoding it.
		prop.put("update-file-list." + oldVersion, NON_STENDHAL_FILES + ",stendhal" + legacy + "-data-diff-" + oldVersion + "-" + newVersion + ".jar,stendhal" + legacy + "-diff-" + oldVersion + "-" + newVersion + ".jar");
	}

	/**
	 * update the file size section
	 */
	private void updateFileSize() {
		// TODO: implement me
	}

	/**
	 * writes the new version of the update.properties
	 *
	 * @throws IOException in case of an input/output error
	 */
	private void writeNewUpdateProperties() throws IOException {
		PrintStream ps = new PrintStream(new FileOutputStream(newFile));
		UpdatePropertiesWriter writer = new UpdatePropertiesWriter(prop, ps);
		writer.process();
		ps.close();
	}

	/**
	 * generates a new update.properties based on an existing one
	 *
	 * @param args oldFile newFile oldVersion newVersion folder [legacy]
	 * @throws IOException in case of an input/output error
	 */
	public static void main(final String[] args) throws IOException {
		if ((args.length != 5) && (args.length != 6)) {
			System.err.println("java " + UpdatePropUpdater.class.getName() + " oldFile newFile oldVersion newVersion folder [legacy]");
			System.exit(1);
		}
		String legacy = "";
		if (args.length > 5) {
			legacy = "-" + args[5];
		}
		UpdatePropUpdater updater = new UpdatePropUpdater(args[0], args[1], args[2], args[3], args[4], legacy);
		updater.process();
	}
}
