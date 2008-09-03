package games.stendhal.tools.updateprop;

import java.io.PrintStream;
import java.util.Properties;

/**
 * Writes an update.properties file
 *
 * @author hendrik
 */
public class UpdatePropertiesWriter {
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
	}

	public void process() {
		header();
		status();
		destination();
		init();
		updateFileList();
	}
/*
#This file contains information required for automatic updates

# Status of this version:
#     OUTDATED: sorry, you have to redownload
#     UPDATE_NEEDED:   there is an update availible
#     CURRENT:  good, we don't have to do anything at the moment
version.0.69=CURRENT
version.0.68.1=OUTDATED
version.0.68=OUTDATED
version.0.67=OUTDATED
version.0.66=OUTDATED
version.0.65=OUTDATED
version.0.64=OUTDATED
version.0.63=OUTDATED
version.0.62=OUTDATED
version.0.61=OUTDATED
version.0.60=OUTDATED
version.0.59.0.2=OUTDATED
version.0.59.0.1=OUTDATED
version.0.59=OUTDATED
version.0.58.1=OUTDATED
version.0.58=OUTDATED
version.0.57.1=OUTDATED
version.0.57=OUTDATED
version.0.56=OUTDATED
version.0.55.1=OUTDATED
version.0.55=OUTDATED
version.0.54.9=OUTDATED
version.0.54.8=OUTDATED
version.0.54.3=OUTDATED
version.0.54.2=OUTDATED
version.0.54.1=OUTDATED
version.0.54=OUTDATED

# new version after update to calculate multiple updates in a row
#version.destination.0.66=0.67

# files to download on first install
init.file-list=log4j.jar,marauroa-2.5.jar,stendhal-data-0.69.jar,stendhal-0.69.jar
init.version=0.69

# files to download on update from this version
#update-file-list.0.67=stendhal-diff-0.67-0.68.jar,stendhal-data-diff-0.67-0.68.jar,marauroa-2.5.jar,log4j.jar

# size of complete files
file-size.log4j.jar=209693
file-size.marauroa-2.5.jar=245198

file-size.stendhal-0.69.jar=559913
file-size.stendhal-data-0.69.jar=18336725

# size of update files
#file-size.stendhal-diff-0.68.1-0.69.jar=428828
#file-size.stendhal-data-diff-0.68.1-0.69.jar=1010978

 */

	private void header() {
		// TODO Auto-generated method stub
		
	}

	private void status() {
		// TODO Auto-generated method stub
		
	}

	private void destination() {
		// TODO Auto-generated method stub
		
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}

	private void updateFileList() {
		// TODO Auto-generated method stub
		
	}
}
