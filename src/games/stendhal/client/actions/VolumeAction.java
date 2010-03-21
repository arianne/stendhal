package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.sound.SoundGroup;
import games.stendhal.common.math.Numeric;

/**
 * Set sound characteristics.
 */
class VolumeAction implements SlashAction {

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
		if (params[0].equals("show")) {
			float volume = ClientSingletonRepository.getSound().getVolume();
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("master -> " + Numeric.floatToInt(volume, 100.0f)));

			for (String name : ClientSingletonRepository.getSound().getGroupNames()) {
				volume = ClientSingletonRepository.getSound().getGroup(name).getVolume();
				ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(name + " -> " + Numeric.floatToInt(volume, 100.0f)));
			}
		} else {
			changeVolume(params[0], params[1]);
		}
		return true;
	}

	private void changeVolume(String groupName, String volumeString) {
		try {
			boolean groupExists = false;

			for (String name : ClientSingletonRepository.getSound().getGroupNames()) {
				if (name.equals(groupName)) {
					groupExists = true;
					break;
				}
			}

			if (groupExists) {
				int volume = Integer.parseInt(volumeString);
				SoundGroup group = ClientSingletonRepository.getSound().getGroup(groupName);
				group.changeVolume(Numeric.intToFloat(volume, 100.0f));
			} else {
				if (groupName.equals("master")) {
					int volume = Integer.parseInt(volumeString);
					ClientSingletonRepository.getSound().changeVolume(Numeric.intToFloat(volume, 100.0f));
				} else {
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("No sound group \"" + groupName + "\" does exist"));
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Please type \"/volume show\" for a valid list of groups"));
				}
			}
		} catch (NumberFormatException exception) {
			ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine(volumeString + " is not a valid number"));
		}
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
