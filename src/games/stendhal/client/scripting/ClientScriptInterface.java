package games.stendhal.client.scripting;

import org.apache.log4j.Logger;


/**
 * Interface used by client side scripts to interact with the game
 *
 * @author hendrik
 */
public class ClientScriptInterface {
	private static Logger logger = Logger.getLogger(ClientScriptInterface.class);

	/**
	 * handles a string command in the same way the chat line does
	 * 
	 * @param input String to parse and handle
	 */
	public void invoke(String input) {
		ChatLineParser.get().parseAndHandle(input);
		sleepMillis(300);
	}
	
	/**
	 * waits the specified number of milliseconds.
	 *
	 * @param millis amount of milliseconds to wait
	 */
	public void sleepMillis(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			logger.error(e, e);
		}
	}

	public void sleepSeconds(long seconds) {
		sleepMillis(seconds * 1000);
	}

	public void sleepTurns(long turns) {
		sleepMillis(turns * 300);
	}
}
