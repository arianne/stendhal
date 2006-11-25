package games.stendhal.client.scripting;


/**
 * Interface used by client side scripts to interact with the game
 *
 * @author hendrik
 */
public class ClientScriptInterface {

	/**
	 * handles a string command in the same way the chat line does
	 * 
	 * @param input String to parse and handle
	 */
	public void invoke(String input) {
		ChatLineParser.get().parseAndHandle(input);
	}
}
