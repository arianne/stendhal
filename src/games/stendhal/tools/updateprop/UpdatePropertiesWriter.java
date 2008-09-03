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
	public UpdatePropertiesWriter(Properties prop, PrintStream ps) {
		this.prop = prop;
		this.ps = ps;
		this.keys = new TreeSet<String>((Set<String>) (Set) prop.keySet());
	}

	public void process() {
		header();
		status();
		destination();
		init();
		updateFileList();
		fileSize();
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

		for (String key : keys) {
			if (key.startsWith("version.")) {
				ps.println(key + "=" + prop.getProperty(key));
			}
		}
		ps.println();
	}

	private void destination() {
		// TODO Auto-generated method stub
		
/*
# new version after update to calculate multiple updates in a row
#version.destination.0.66=0.67
 */
	}
	

	private void init() {
		// TODO Auto-generated method stub

/*
		# files to download on first install
		init.file-list=log4j.jar,marauroa-2.5.jar,stendhal-data-0.69.jar,stendhal-0.69.jar
		init.version=0.69
*/
	}

	private void updateFileList() {
		// TODO Auto-generated method stub

/*
		# files to download on update from this version
		#update-file-list.0.67=stendhal-diff-0.67-0.68.jar,stendhal-data-diff-0.67-0.68.jar,marauroa-2.5.jar,log4j.jar
*/
	}

	private void fileSize() {
		// TODO Auto-generated method stub
		
/*
# size of complete files
file-size.log4j.jar=209693
file-size.marauroa-2.5.jar=245198

file-size.stendhal-0.69.jar=559913
file-size.stendhal-data-0.69.jar=18336725

# size of update files
#file-size.stendhal-diff-0.68.1-0.69.jar=428828
#file-size.stendhal-data-diff-0.68.1-0.69.jar=1010978
 */
	}
}
