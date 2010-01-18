package games.stendhal.bot.textclient;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

/**
 * the text user interface
 *
 * @author hendrik
 */
public class TextUI implements UserInterface {
	private static final String ESC_COLOR_RESET = "\u001B[m";
	private static final String ESC_COLOR_INPUT = "\u001B[32m";

	/**
	 * creates a TextUI
	 */
	public TextUI() {
		ClientSingletonRepository.setUserInterface(this);
	}

	/**
	 * just output a line on stdout
	 *
	 * @param line to print
	 */
	public void addEventLine(EventLine line) {
		System.out.println(ESC_COLOR_RESET + line + ESC_COLOR_INPUT);
	}

	/**
	 * adds a text box on the screen
	 *
	 * @param x  x
	 * @param y  y
	 * @param text text to display
	 * @param type type of text
	 * @param isTalking chat?
	 */
	public void addGameScreenText(double x, double y, 
			String text, NotificationType type,
			boolean isTalking) {
		// ignored
		
	}

}
