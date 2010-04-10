package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.BareBonesBrowserLaunch;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;
/**
 * generalized super class to provide a uniform way to open urls in the browser
 * @author madmetzger
 *
 */
public class BareBonesBrowserLaunchCommand implements SlashAction{
	
	protected final String urlToOpen;

	public BareBonesBrowserLaunchCommand(String url) {
		urlToOpen = url;
	}

	/**
	 * Opens an URL with the browser
	 * 
	 * @param params ignored
	 * @param remainder ignored
	 * @return <code>true</code>
	 */
	public boolean execute(final String[] params, final String remainder) {
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(
				"Trying to open #"+urlToOpen+" in your browser.",
		NotificationType.CLIENT));
	
		BareBonesBrowserLaunch.openURL(urlToOpen);
	
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