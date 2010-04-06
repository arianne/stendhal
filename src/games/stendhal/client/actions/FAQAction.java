package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Opens the FAQ
 */
class FAQAction implements SlashAction {

	/**
	 * Opens the faq
	 * 
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	public boolean execute(final String[] params, final String remainder) {
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #http://stendhalgame.org/wiki/StendhalFAQ in your browser.",
		NotificationType.CLIENT));

		BareBonesBrowserLaunch.openURL("http://stendhalgame.org/wiki/StendhalFAQ");

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
