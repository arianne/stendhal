package games.stendhal.client.update;

import games.stendhal.common.Version;

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
	private Properties fileList = null;

	/**
	 * Connects to the server and loads a Property object which contains
	 * information about the files available for update.
	 */
	private void init() {
		HttpClient httpClient = new HttpClient(SERVER_FOLDER + "update.properties");
		fileList = httpClient.fetchProperties();
	}

	public void process() {
		init();
		if (fileList == null) {
			return;
		}
		String versionStateString = fileList.getProperty("version." + Version.VERSION);
		VersionState versionState = VersionState.getFromString(versionStateString);

		
		fileList.list(System.out);
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

	// debug code
	public static void main(String args[]) {
		Log4J.init("data/conf/log4j.properties");
		UpdateManager um = new UpdateManager();
		um.process();
	}
}
