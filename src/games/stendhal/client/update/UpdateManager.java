package games.stendhal.client.update;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * manages downloading and installing of updates
 *
 * @author hendrik
 */
public class UpdateManager {
	private String jarFolder = null;
	private Properties bootProp = null;
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
		jarFolder = Bootstrap.get().getJarFolder();
		bootProp = Bootstrap.get().getBootProp();
	}

	/**
	 * Processes the update
	 */
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
			case INITIAL_DOWNLOAD: {
				List<String> files = getFilesForFirstDownload();
				int updateSize = getSizeOfFilesToUpdate(files);
				if (UpdateGUI.askForDownload(updateSize, false)) {
					if (downloadFiles(files)) {
						updateClasspathConfig(files);
					}
				}
				break;
			}
			case UPDATE_NEEDED: {
				List<String> files = getFilesToUpdate();
				int updateSize = getSizeOfFilesToUpdate(files);
				if (UpdateGUI.askForDownload(updateSize, true)) {
					if (downloadFiles(files)) {
						updateClasspathConfig(files);
					}
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
	 * returns the list of all files to download for the first install
	 *
	 * @return list of files
	 */
	private List<String> getFilesForFirstDownload() {
		List<String> res = new LinkedList<String>();
		String list = updateProp.getProperty("file-list");
		res.addAll(Arrays.asList(list.split(",")));

		while (res.contains("")) {
			res.remove("");
		}
		return res;
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

	/**
	 * Downloads the files listed for update
	 *
	 * @param files list of files to download
	 * @return true on success, false otherwise
	 */
	private boolean downloadFiles(List<String> files) {
		for (String file : files) {
			HttpClient httpClient = new HttpClient(SERVER_FOLDER + file);
			if (!httpClient.fetchFile(jarFolder + file)) {
				UpdateGUI.messageBox("Sorry, an error occured while downloading the update at file " + file);
				return false;
			}
			try {
				File fileObj = new File(jarFolder + file);
				int shouldSize = Integer.parseInt(updateProp.getProperty("file-size." + file, ""));
				if (fileObj.length() != shouldSize) {
					UpdateGUI.messageBox("Sorry, an error occured while downloading the update. File size of "
									+ file + " does not match. We got " + fileObj.length() + " but it should be " + shouldSize);
					return false;
				}
			} catch (NumberFormatException e) {
				logger.warn(e, e);
			}			
		}
		return true;
	}

	/**
	 * updates the classpath
	 *
	 * @param files
	 */
	private void updateClasspathConfig(List<String> files) {
		// invert order of files so that the newer ones are first on classpath
		Collections.reverse(files);
		StringBuilder sb = new StringBuilder();
		for (String file : files) {
			sb.append("," + file);
		}
		bootProp.put("load", bootProp.getProperty("load", "") + sb.toString());
		try {
			Bootstrap.get().saveBootProp();
		} catch (IOException e) {
			UpdateGUI.messageBox("Sorry, an error occured while downloading the update. Could not write bootProperties");
		}
	}

}
