package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.SoundSystem;

/**
 * Set sound characteristics.
 */
class SoundCommand implements SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String [] params, String remainder) {
		String command = params[0];


		if(command.equals("mute")) {
			String param = params[1];

			WtWindowManager.getInstance().setProperty(
				"sound.mute", param);

			SoundSystem.get().setMute(param.equals("on"));
		} else if(command.equals("volume")) {
			int vol;

			try {
				vol = Integer.parseInt(params[1]);
			} catch(NumberFormatException ex) {
				vol = -1;
			}

			if ((vol < 0) || (vol > 100)) {
				StendhalClient.get().addEventLine("Volume must be an integer between 0 and 100");
			} else {
				WtWindowManager.getInstance().setProperty("sound.volume", Integer.toString(vol));
				SoundSystem.get().setVolume(vol);
			}
		}

		return true;
	}


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 2;
	}
}