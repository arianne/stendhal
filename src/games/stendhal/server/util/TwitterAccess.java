package games.stendhal.server.util;

import java.io.IOException;

import marauroa.common.Configuration;

import org.apache.log4j.Logger;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;


/**
 * Accesses twitter
 *
 * @author hendrik
 */
public class TwitterAccess {
	private static Logger logger = Logger.getLogger(TwitterAccess.class);

	/**
	 * Sends a tweet
	 *
	 * @param account account to use
	 * @param message message to tweet
	 */
	public static void tweet(String account, String message) {
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
		
        AsyncTwitterFactory factory = new AsyncTwitterFactory(new TwitterAdapter() {
            @Override
            public void onException(TwitterException e, TwitterMethod method) {
            	logger.error("Twitter error", e);
            }
        });

        // get username and password from configuratoin
        String username = configuration.get("stendhal.twitter." + account + ".username", account);
        String password = configuration.get("stendhal.twitter." + account + ".password");

        // send the tweet
        AsyncTwitter twitter = factory.getInstance(username, password);
        twitter.updateStatus(message);

	}
}
