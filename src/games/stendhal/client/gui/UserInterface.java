package games.stendhal.client.gui;

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

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

	/**
	 * adds a text box on the screen
	 *
	 * @param x  x
	 * @param y  y
	 * @param text text to display
	 * @param type type of text
	 * @param isTalking chat?
	 */
	public void addGameScreenText(final double x, final double y, 
			final String text, final NotificationType type,
			final boolean isTalking);
}
