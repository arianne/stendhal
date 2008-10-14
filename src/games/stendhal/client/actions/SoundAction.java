package games.stendhal.client.actions;

import games.stendhal.client.StendhalUI;
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
			final String param = params[1];

			WtWindowManager.getInstance().setProperty("sound.mute", param);
			SoundMaster.setMute(param.equals("on"));
			SoundSystem.get().setMute(param.equals("on"));
		} else if (command.equals("volume")) {
			int vol;

			try {
				vol = Integer.parseInt(params[1]);
			} catch (final NumberFormatException ex) {
				vol = -1;
			}

			if ((vol < 0) || (vol > 100)) {
				StendhalUI.get().addEventLine(new StandardEventLine(
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
		return 2;
	}
}
