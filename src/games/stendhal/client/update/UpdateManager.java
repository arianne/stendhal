package games.stendhal.client.update;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Manages download and installation of updates.
 * 
 * @author hendrik
 */
public class UpdateManager {

	private String jarFolder;

	private Properties bootProp;

	private String serverFolder;

	private Properties updateProp;

	private UpdateProgressBar updateProgressBar;

	/**
	 * Connects to the server and loads a Property object which contains
	 * information about the files available for update.
	 * @param initialDownload 
	 */
	private void init(final boolean initialDownload) {
		String updatePropertiesFile = ClientGameConfiguration.get("UPDATE_SERVER_FOLDER")
				+ "/update-0.86.properties";
		if (bootProp != null) {
			serverFolder = bootProp.getProperty("server.folder-0.86",
					ClientGameConfiguration.get("UPDATE_SERVER_FOLDER"))
					+ "/";
			updatePropertiesFile = bootProp.getProperty("server.update-prop-0.86",
					serverFolder + "update-0.86.properties");
		}
		final HttpClient httpClient = new HttpClient(updatePropertiesFile,
				initialDownload);
		updateProp = httpClient.fetchProperties();
	}

	/**
	 * Processes the update.
	 * 
	 * @param jarFolder
	 *            folder where the .jar files are stored
	 * @param bootProp
	 *            boot properties
	 * @param initialDownload
	 *            true, if only the small starter.jar is available
	 */
	public void process(final String jarFolder, final Properties bootProp,
			final Boolean initialDownload) {
		if (!Boolean.parseBoolean(ClientGameConfiguration.get("UPDATE_ENABLE_AUTO_UPDATE"))) {
			System.out.println("Automatic Update disabled");
			return;
		}
		this.jarFolder = jarFolder;
		this.bootProp = bootProp;
		init(initialDownload.booleanValue());
		if (updateProp == null) {
			if (initialDownload.booleanValue()) {
				UpdateGUIDialogs.messageBox("Sorry, we need to download additional files from "
						+ serverFolder
						+ " but that server is not reachable at the moment. Please try again later.");
				System.exit(1);
			}
			return;
		}
		VersionState versionState = null;
		if (initialDownload.booleanValue()) {
			versionState = VersionState.INITIAL_DOWNLOAD;
		} else {
			final String versionStateString = updateProp.getProperty("version."
					+ Version.VERSION);
			versionState = VersionState.getFromString(versionStateString);
		}

		switch (versionState) {
		case CURRENT:
			System.out.println("Current Version");
			break;

		case ERROR:
			UpdateGUIDialogs.messageBox("An error occurred while trying to update");
			break;

		case OUTDATED:
			UpdateGUIDialogs.messageBox("Sorry, your client is too outdated for the update to work. Please download the current version.");
			break;

		case INITIAL_DOWNLOAD:
			List<String> files = getFilesForFirstDownload();
			String version = updateProp.getProperty("init.version");
			// just check if there is already an update for the inital version
			if (version != null) {
				files.addAll(getFilesToUpdate(version));
			}
			List<String> filesToAddToClasspath = new ArrayList<String>(files);
			removeAlreadyExistingFiles(files);
			int updateSize = getSizeOfFilesToUpdate(files);
			if ((updateSize == 0) || (UpdateGUIDialogs.askForDownload(updateSize, false))) {
				if (downloadFiles(files, updateSize)) {
					updateClasspathConfig(filesToAddToClasspath);
				}
			} else {
				System.exit(1);
			}
			break;

		case UPDATE_NEEDED:
			version = Version.VERSION;
			files = getFilesToUpdate(version);
			filesToAddToClasspath = new ArrayList<String>(files);
			removeAlreadyExistingFiles(files);
			updateSize = getSizeOfFilesToUpdate(files);
			if ((updateSize == 0) || (UpdateGUIDialogs.askForDownload(updateSize, true))) {
				if (downloadFiles(files, updateSize)) {
					updateClasspathConfig(filesToAddToClasspath);
				}
			}
			break;

		case UNKNOWN:
			System.out.println("Unknown state of update");
			break;

		default:
			System.out.println("Internal Error on Update");
			break;

		}
	}

	/**
	 * Removes all files from the download list which have already been
	 * downloaded.
	 * 
	 * @param files
	 *            list of files to check and clean
	 */
	private void removeAlreadyExistingFiles(final List<String> files) {
		final Iterator<String> itr = files.iterator();
		while (itr.hasNext()) {
			final String file = itr.next();
			if (file.trim().equals("")) {
				itr.remove();
				continue;
			}
			try {
				// TODO: use hash of files instead of size
				final long sizeShould = Integer.parseInt(updateProp.getProperty(
						"file-size." + file, ""));
				final long sizeIs = new File(jarFolder + file).length();
				if (sizeShould == sizeIs) {
					itr.remove();
				}
			} catch (final RuntimeException e) {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * returns the list of all files to download for the first install.
	 * 
	 * @return list of files
	 */
	private List<String> getFilesForFirstDownload() {
		final List<String> res = new LinkedList<String>();
		final String list = updateProp.getProperty("init.file-list");
		res.addAll(Arrays.asList(list.split(",")));

		while (res.contains("")) {
			res.remove("");
		}
		return res;
	}

	/**
	 * returns the list of all files to download for transitive update.
	 * 
	 * @param startVersion
	 *            the version to start the path at
	 * @return list of files
	 */
	private List<String> getFilesToUpdate(final String startVersion) {
		final List<String> res = new LinkedList<String>();

		String version = startVersion;
		while (true) {
			final String list = updateProp.getProperty("update-file-list." + version);
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
	 * Calculates the sum of the file sizes.
	 * 
	 * @param files
	 *            list of files
	 * @return total size of download
	 */
	private int getSizeOfFilesToUpdate(final List<String> files) {
		int res = 0;
		for (final String file : files) {
			try {
				res = res
						+ Integer.parseInt(updateProp.getProperty("file-size."
								+ file, ""));
			} catch (final NumberFormatException e) {
				e.printStackTrace(System.err);
			}
		}
		return res;
	}

	/**
	 * Downloads the files listed for update.
	 * 
	 * @param files
	 *            list of files to download
	 * @param size
	 *            file size
	 * @return true on success, false otherwise
	 */
	private boolean downloadFiles(final List<String> files, final int size) {
		updateProgressBar = new UpdateProgressBar(size);
		updateProgressBar.setVisible(true);
		for (final String file : files) {
			boolean res = downloadFile(file);
			if (!res) {
				return false;
			}
		}
		updateProgressBar.dispose();
		return true;
	}

	/**
	 * Downloads the specified file
	 *
	 * @param file name of file
	 * @return true, if the download was successful, false otherwise
	 */
	private boolean downloadFile(final String file) {
		boolean res = tryDownloadFromPreferredLocation(file);
		if (res) {
			return true;
		}
		System.out.println("Downloading " + file + " ...");
		final HttpClient httpClient = new HttpClient(serverFolder + file, true);
		httpClient.setProgressListener(updateProgressBar);
		if (!httpClient.fetchFile(jarFolder + file)) {
			UpdateGUIDialogs.messageBox("Sorry, an error occurred while downloading the update at file "
					+ file);
			return false;
		}
		try {
			final File fileObj = new File(jarFolder + file);
			final int shouldSize = Integer.parseInt(updateProp.getProperty(
					"file-size." + file, ""));
			if (fileObj.length() != shouldSize) {
				UpdateGUIDialogs.messageBox("Sorry, an error occurred while downloading the update. File size of "
						+ file
						+ " does not match. We got "
						+ fileObj.length()
						+ " but it should be "
						+ shouldSize);
				updateProgressBar.dispose();
				return false;
			}
		} catch (final NumberFormatException e) {
			e.printStackTrace(System.err);
			updateProgressBar.dispose();
			return false;
		}
		return true;
	}

	/**
	 * tries to download the file from a preferred, but less stable location.
	 * errors are only logged to stdout and not displayed to the user because
	 * if this download fails, the one from the normal location is tried.
	 *
	 * @param file name of file to download
	 * @return true, if the download was successful; false otherwise
	 */
	private boolean tryDownloadFromPreferredLocation(String file) {

		// is a preferred download location specified?
		String preferredLocationFolder = updateProp.getProperty("location.preferred.folder");
		String preferredLocationSuffix = updateProp.getProperty("location.preferred.suffix", "");
		System.out.println("checking for preferred location: preferredLocationFolder=" + preferredLocationFolder + " preferredLocationSuffix=" + preferredLocationSuffix);
		if (preferredLocationFolder == null) {
			// no preferred location specified
			return false;
		}

		// try to download
		System.out.println("Downloading " + file + " from preferred location...");
		final HttpClient httpClient = new HttpClient(preferredLocationFolder + file + preferredLocationSuffix, false);
		httpClient.setProgressListener(updateProgressBar);
		if (!httpClient.fetchFile(jarFolder + file)) {
			System.out.println("fetch file failed, will retry from normal location");
			return false;
		}

		// check file size
		try {
			final File fileObj = new File(jarFolder + file);
			final int shouldSize = Integer.parseInt(updateProp.getProperty("file-size." + file, ""));
			if (fileObj.length() != shouldSize) {
				System.out.println("wrong file size, will retry from normal location");
				return false;
			}
		} catch (final NumberFormatException e) {
			e.printStackTrace(System.err);
			return false;
		}

		// all okay
		return true;
	}

	/**
	 * Updates the classpath.
	 * 
	 * @param files
	 */
	private void updateClasspathConfig(final List<String> files) {
		// invert order of files so that the newer ones are first on classpath
		Collections.reverse(files);
		final StringBuilder sb = new StringBuilder();
		for (final String file : files) {
			sb.append(file + ",");
		}

		sb.append(bootProp.getProperty("load-0.86", ""));

		bootProp.put("load-0.86", sb.toString());
	}
}
