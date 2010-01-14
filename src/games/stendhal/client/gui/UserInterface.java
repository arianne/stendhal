package games.stendhal.client.gui;

import games.stendhal.client.gui.chatlog.EventLine;

/**
 * @author hendrik
 *
 */
public interface UserInterface {

	/**
	 * Add an event line to the chat log.
	 *
	 * @param line event line
	 */
	public void addEventLine(final EventLine line);

}
