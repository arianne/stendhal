package games.stendhal.client.update;

import games.stendhal.common.Version;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * manages downloading and installing of updates
 *
 * @author hendrik
 */
public class UpdateManager {
	// TODO: fix URL after testing is completed
	private static final String SERVER_FOLDER = "http://localhost/stendhal/updates/";
	private static Logger logger = Logger.getLogger(UpdateManager.class);
	private Properties updateProp = null;

	/**
	 * Connects to the server and loads a Property object which contains
	 * information about the files available for update.
	 */
	private void init() {
		HttpClient httpClient = new HttpClient(SERVER_FOLDER + "update.properties");
		updateProp = httpClient.fetchProperties();
	}

	public void process() {
		init();
		if (updateProp == null) {
			return;
		}
		String versionStateString = updateProp.getProperty("version." + Version.VERSION);
		VersionState versionState = VersionState.getFromString(versionStateString);

		
		updateProp.list(System.out);
		logger.info(Version.VERSION);
		logger.info(versionState);

		switch (versionState) {
			case CURRENT: {
				logger.info("Current Version");
				break;
			}
			case ERROR: {
				UpdateGUI.messageBox("An error occured while trying to update");
				break;
			}
			case OUTDATED: {
				UpdateGUI.messageBox("Sorry, your client is too outdated for the update to work. Please download the current version.");
				break;
			}
			case UPDATE_NEEDED: {
				if (UpdateGUI.askForUpdate()) {
					UpdateGUI.messageBox("Doing update");
				}
				break;
			}
			case UNKOWN: {
				logger.info("Unkown state of update");
				break;
			}
			default: {
				logger.error("Internal Error on Update");
				break;
			}
		}
	}

	/**
	 * returns the list of all files to download for transitive update
	 *
	 * @return list of files
	 */
	private List<String> getFilesToUpdate() {
		List<String> res = new LinkedList<String>();
		String version = Version.VERSION;
		
		while (true) {
			String list = updateProp.getProperty("update-file-list." + version);
			if (list == null) {
				break;
			}
			res.addAll(Arrays.asList(list.split(",")));
			version = updateProp.getProperty("version.destination." + version);
		}
		
		while (res.contains("")) {
			res.remove("");
		}
		return res;
	}

	/**
	 * calculates the sum of the file sizes
	 *  
	 * @param files list of files
	 * @return total size of download
	 */
	private int getSizeOfFilesToUpdate(List<String> files) {
		int res = 0;
		for (String file : files) {
			try {
				res = res + Integer.parseInt(updateProp.getProperty("file-size." + file, ""));
			} catch (NumberFormatException e) {
				logger.warn(e, e);
			}
		}
		return res;
	}

	// debug code
	public static void main(String args[]) {
		Log4J.init("data/conf/log4j.properties");
		UpdateManager um = new UpdateManager();
		um.process();
	}
}
