package games.stendhal.tools.updateprop;

import java.io.PrintStream;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Writes an update.properties file
 *
 * @author hendrik
 */
public class UpdatePropertiesWriter {
	private Set<String> keys;
	private Properties prop;
	private PrintStream ps;

	/**
	 * Creates a new UpdatePropertiesWriter
	 *
	 * @param prop properties to write
	 * @param ps   output stream
	 */
	public UpdatePropertiesWriter(final Properties prop, final PrintStream ps) {
		this.prop = prop;
		this.ps = ps;
		this.keys = new TreeSet<String>((Set) prop.keySet());
	}

	public void process() {
		header();
		status();
		destination();
		init();
		updateFileList();
		fileSize();
	}

	private void writeKeysWithPrefix(String prefix) {
		for (String key : keys) {
			if (key.startsWith(prefix)) {
				ps.println(key + "=" + prop.getProperty(key));
			}
		}
		ps.println();
	}

	private void header() {
		ps.println("# This file contains information required for automatic updates");
		ps.println();
	}

	private void status() {
		ps.println("# Status of this version:");
		ps.println("#     OUTDATED: sorry, you have to redownload");
		ps.println("#     UPDATE_NEEDED:   there is an update available");
		ps.println("#     CURRENT:  good, we don't have to do anything at the moment");

		writeKeysWithPrefix("version.");
	}

	private void destination() {
		ps.println("# new version after update to calculate multiple updates in a row");
		writeKeysWithPrefix("version.destination");
	}
	

	private void init() {
		ps.println("# files to download on first install");
		writeKeysWithPrefix("init.");
	}

	private void updateFileList() {
		ps.println("# files to download on update from this version");
		writeKeysWithPrefix("update-file-list.");
	}

	private void fileSize() {
		ps.println("# size of complete files");
		writeKeysWithPrefix("file-size.");
		// TODO: Sort this in a nicer way
	}
}
