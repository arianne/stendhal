package games.stendhal.client.update;


import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * manages downloading and installing of updates
 *
 * @author hendrik
 */
public class UpdateManager {
	private String jarFolder = null;
	private Properties bootProp = null;
	private static final String DEFAULT_SERVER_FOLDER = "http://arianne.sourceforge.net/stendhal/updates/";
	private String serverFolder = DEFAULT_SERVER_FOLDER;
	private Properties updateProp = null;
	private UpdateProgressBar updateProgressBar = null;

	/**
	 * Connects to the server and loads a Property object which contains
	 * information about the files available for update.
	 */
	private void init(boolean initialDownload) {
		String updatePropertiesFile = serverFolder + "update.properties";
		if (bootProp != null) {
			serverFolder = bootProp.getProperty("server.folder", DEFAULT_SERVER_FOLDER);
			updatePropertiesFile = bootProp.getProperty("server.update-prop", serverFolder + "update.properties");
		}
		HttpClient httpClient = new HttpClient(updatePropertiesFile, initialDownload);
		updateProp = httpClient.fetchProperties();
	}

	/**
	 * Processes the update
	 *
	 * @param jarFolder folder where the .jar files are stored
	 * @param bootProp  boot properties
	 * @param initialDownload true, if only the small starter.jar is available
	 */
	public void process(String jarFolder, Properties bootProp, Boolean initialDownload) {
		this.jarFolder = jarFolder;
		this.bootProp = bootProp;
		init(initialDownload.booleanValue());
		if (updateProp == null) {
			if (initialDownload.booleanValue()) {
				UpdateGUIDialogs.messageBox("Sorry, we need to download additional files from " + serverFolder + " but that server is not reachable at the moment. Please try again later.");
				System.exit(1);
			}
			return;
		}
		VersionState versionState = null;
		if (initialDownload.booleanValue()) {
			versionState = VersionState.INITIAL_DOWNLOAD;
		} else {
			String versionStateString = updateProp.getProperty("version." + Version.VERSION);
			versionState = VersionState.getFromString(versionStateString);
		}

		switch (versionState) {
			case CURRENT: {
				System.out.println("Current Version");
				break;
			}
			case ERROR: {
				UpdateGUIDialogs.messageBox("An error occured while trying to update");
				break;
			}
			case OUTDATED: {
				UpdateGUIDialogs.messageBox("Sorry, your client is too outdated for the update to work. Please download the current version.");
				break;
			}
			case INITIAL_DOWNLOAD: {
				List<String> files = getFilesForFirstDownload();
				String version = updateProp.getProperty("init.version");
				// just check if there is already an update for the inital version
				if (version != null) {
					files.addAll(getFilesToUpdate(version));
				}
				int updateSize = getSizeOfFilesToUpdate(files);
				if (UpdateGUIDialogs.askForDownload(updateSize, false)) {
					if (downloadFiles(files, updateSize)) {
						updateClasspathConfig(files);
					}
				} else {
					System.exit(1);
				}
				break;
			}
			case UPDATE_NEEDED: {
				String version = Version.VERSION;
				List<String> files = getFilesToUpdate(version);
				int updateSize = getSizeOfFilesToUpdate(files);
				if (UpdateGUIDialogs.askForDownload(updateSize, true)) {
					if (downloadFiles(files, updateSize)) {
						updateClasspathConfig(files);
					}
				}
				break;
			}
			case UNKOWN: {
				System.out.println("Unkown state of update");
				break;
			}
			default: {
				System.out.println("Internal Error on Update");
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
		String list = updateProp.getProperty("init.file-list");
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
	private List<String> getFilesToUpdate(String version) {
		List<String> res = new LinkedList<String>();
		
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
				e.printStackTrace(System.err);
			}
		}
		return res;
	}

	/**
	 * Downloads the files listed for update
	 *
	 * @param files list of files to download
	 * @param size file size
	 * @return true on success, false otherwise
	 */
	private boolean downloadFiles(List<String> files, int size) {
		updateProgressBar = new UpdateProgressBar(size);
		updateProgressBar.setVisible(true);
		for (String file : files) {
			System.out.println("Downloading " + file + " ...");
			HttpClient httpClient = new HttpClient(serverFolder + file, true);
			httpClient.setProgressListener(updateProgressBar);
			if (!httpClient.fetchFile(jarFolder + file)) {
				UpdateGUIDialogs.messageBox("Sorry, an error occured while downloading the update at file " + file);
				return false;
			}
			try {
				File fileObj = new File(jarFolder + file);
				int shouldSize = Integer.parseInt(updateProp.getProperty("file-size." + file, ""));
				if (fileObj.length() != shouldSize) {
					UpdateGUIDialogs.messageBox("Sorry, an error occured while downloading the update. File size of "
									+ file + " does not match. We got " + fileObj.length() + " but it should be " + shouldSize);
					updateProgressBar.dispose();
					return false;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace(System.err);
				updateProgressBar.dispose();
				return false;
			}			
		}
		updateProgressBar.dispose();
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
			sb.append(file + ",");
		}
		bootProp.put("load", sb.toString() + bootProp.getProperty("load", ""));
	}
}
