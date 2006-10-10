package games.stendhal.client.update;

import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * manages downloading and installing of updates
 *
 * @author hendrik
 */
public class UpdateManager {
	// TODO: fix URL after testing is completed
	private static final String SERVER_FOLDER = "http://localhost/stendhal/";
	private static Logger logger = Logger.getLogger(UpdateManager.class);
	private Properties fileList = null;

	/**
	 * Connects to the server and loads a Property object which contains
	 * information about the files available for update.
	 */
	public void init() {
		HttpClient httpClient = new HttpClient(SERVER_FOLDER + "file-list.properties");
		fileList = httpClient.fetchProperties();
	}

}
