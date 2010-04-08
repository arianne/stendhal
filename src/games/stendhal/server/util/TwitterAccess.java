package games.stendhal.server.util;

import games.stendhal.common.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import marauroa.common.Configuration;

import org.apache.log4j.Logger;

/**
 * Accesses twitter
 * 
 * @author hendrik
 */
public class TwitterAccess extends Thread {
	private static Logger logger = Logger.getLogger(TwitterAccess.class);
	private String message;
	private String account;

	/**
	 * Sends a tweet
	 * 
	 * @param account
	 *            account to use
	 * @param message
	 *            message to tweet
	 */
	public TwitterAccess(String account, String message) {
		this.account = account;
		this.message = message;
	}

	/**
	 * Sends a tweet. Use "start()" for asynchronous access.
	 * 
	 */
	public void run() {
		Configuration configuration;
		try {
			configuration = Configuration.getConfiguration();
		} catch (IOException e1) {
			logger.error(e1, e1);
			return;
		}

		// check that a password for this account was configured
		if (!configuration.has("stendhal.twitter." + account + ".password")) {
			return;
		}

		// get username and password from configuratoin
		String username = configuration.get("stendhal.twitter." + account + ".username", account);
		String password = configuration.get("stendhal.twitter." + account + ".password");

		// send the tweet
		send(username, password, message);
	}

	/**
	 * sends the message to the twitter account
	 *
	 * @param username username of the twitter account
	 * @param password password of the twitter account
	 * @param message message to tweet
	 */
	private void send(String username, String password, String message) {
		try {
			URL url = new URL("https://api.twitter.com/1/statuses/update.xml");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Setup the header for the request
			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", "Stendhal - http://stendhalgame.org");
			String input = username + ":" + password;
			connection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode(input.getBytes("UTF-8"))));

			// write the message
			connection.setDoOutput(true);
			OutputStream os = connection.getOutputStream();
			os.write(("status=" + message.substring(0, Math.min(message.length(), 139))).getBytes("UTF-8"));
			os.close();

			// send the request
			connection.getInputStream().close();
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
}
