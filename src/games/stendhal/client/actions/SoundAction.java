package games.stendhal.client.actions;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.soundreview.SoundMaster;

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
			boolean play = Boolean.parseBoolean(WtWindowManager.getInstance().getProperty("sound.play", "true"));
			play = !play;
			WtWindowManager.getInstance().setProperty("sound.play", Boolean.toString(play));
			SoundMaster.setMute(!play);
			SoundSystem.get().setMute(!play);
		} else if (command.equals("volume")) {
			if (params.length < 2) {
				j2DClient.get().addEventLine(new StandardEventLine(
				"/sound volume <vol>, with <vol> between 0 and 100."));
			}
			int vol;

			try {
				vol = Integer.parseInt(params[1]);
			} catch (final NumberFormatException ex) {
				vol = -1;
			}

			if ((vol < 0) || (vol > 100)) {
				j2DClient.get().addEventLine(new StandardEventLine(
						"Volume must be an integer between 0 and 100"));
			} else {
				WtWindowManager.getInstance().setProperty("sound.volume",
						Integer.toString(vol));
				SoundSystem.get().setVolume(vol);
			}
		}

		return true;
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
