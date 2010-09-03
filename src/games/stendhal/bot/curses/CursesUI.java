package games.stendhal.bot.curses;

import jcurses.util.Protocol;
import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.common.NotificationType;

/**
 * the curses user interface
 *
 * @author hendrik
 */
public class CursesUI implements UserInterface {
	private SoundSystemFacade soundSystemFacade;
	private CursesWindow window;

	/**
	 * creates a CursesUI
	 *
	 * @param window CursesWindow
	 */
	public CursesUI(CursesWindow window) {
		ClientSingletonRepository.setUserInterface(this);
		soundSystemFacade = new NoSoundFacade();
		this.window = window;
	}

	/**
	 * just output a line on stdout
	 *
	 * @param line to print
	 */
	public void addEventLine(EventLine line) {
        Protocol.debug("addEventLine (" + window + "): " + line);
        if (this.window != null) {
            this.window.addChatLine(line.toString());
        }
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

	/**
	 * gets the sound system
	 *
	 * @return SoundSystemFacade
	 */
	public SoundSystemFacade getSoundSystemFacade() {
		return soundSystemFacade;
	}

}
