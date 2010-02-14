package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Opens the faq
 */
class ManualAction implements SlashAction {

	/**
	 * Opens the manual
	 * 
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	public boolean execute(final String[] params, final String remainder) {
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #http://arianne.sourceforge.net/wiki/index.php/StendhalManual in your browser.",
		NotificationType.CLIENT));

		BareBonesBrowserLaunch.openURL("http://stendhal.game-host.org/wiki/index.php/StendhalManual");

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
