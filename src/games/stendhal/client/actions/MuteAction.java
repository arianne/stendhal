package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sound.system.Time;


/**
 * Set sound characteristics.
 */
class MuteAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		play = !play;
		WtWindowManager.getInstance().setProperty("sound.play", Boolean.toString(play));
		SoundSystemFacade.get().mute(!play, new Time(2, Time.Unit.SEC));
		if (play) {
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Sounds are now on."));
		} else {
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Sounds are now off. (You may need to change the zone to stop the background music)."));
		}
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
