package games.stendhal.bot.textclient;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.EventLine;

/**
 * the text user interface
 *
 * @author hendrik
 */
public class TextUI implements UserInterface {

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
		System.out.println(line);
	}

}
