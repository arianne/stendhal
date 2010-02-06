package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.common.MathHelper;

/**
 * Set sound characteristics.
 */
class SoundAction implements SlashAction {

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
		final String command = params[0];

		if (command.equals("mute")) {
			toggleMute();
		} else if (command.equals("volume")) {
			volume(params);
		}

		return true;
	}

	/**
	 * toggles the mute state of sounds.
	 */
	void toggleMute() {
		boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
		play = !play;
		WtWindowManager.getInstance().setProperty("sound.play", Boolean.toString(play));
		SoundSystemFacade.get().setMute(!play);
	}

	/**
	 * sets the volume of sounds
	 *
	 * @param params volume, &lt;vol&gt;
	 */
	private void volume(final String[] params) {
		if (params.length < 2) {
			j2DClient.get().addEventLine(new StandardEventLine(
			"/sound volume <vol>, with <vol> between 0 and 100."));
			return;
		}

		int vol = MathHelper.parseIntDefault(params[1], -1);

		if ((vol < 0) || (vol > 100)) {
			j2DClient.get().addEventLine(new StandardEventLine(
					"Volume must be an integer between 0 and 100"));
			return;
		}

		WtWindowManager.getInstance().setProperty("sound.volume", Integer.toString(vol));
		// TODO: SoundSystem.get().setVolume(vol);
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
