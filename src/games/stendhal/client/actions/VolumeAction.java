/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.chatlog.StandardEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.common.NotificationType;
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
	@Override
	public boolean execute(final String[] params, final String remainder) {
		if (params[0] == null) {
			float volume = ClientSingletonRepository.getSound().getVolume();
			UserInterface ui = ClientSingletonRepository.getUserInterface();
			ui.addEventLine(new StandardEventLine("Please use /volume <name> <value> to adjust the volume."));
			ui.addEventLine(new HeaderLessEventLine("<name> is an item from the following list. \"master\" refers to the global volume setting.", NotificationType.CLIENT));
			ui.addEventLine(new HeaderLessEventLine("<value> is in the range from 0 to 100 but may be set higher.", NotificationType.CLIENT));
			ui.addEventLine(new HeaderLessEventLine("master -> " + Numeric.floatToInt(volume, 100.0f), NotificationType.CLIENT));

			for (String name : ClientSingletonRepository.getSound().getGroupNames()) {
				volume = ClientSingletonRepository.getSound().getGroup(name).getVolume();
				ui.addEventLine(new HeaderLessEventLine(name + " -> " + Numeric.floatToInt(volume, 100.0f), NotificationType.CLIENT));
			}
		} else if (params[1] != null) {
			changeVolume(params[0], params[1]);
		} else {
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine("Please use /volume for help.",
					NotificationType.ERROR));
		}
		return true;
	}

	/**
	 * changes the volume for the specified group
	 *
	 * @param groupName name of group
	 * @param volumeString new volume
	 */
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
				WtWindowManager.getInstance().setProperty("sound.volume." + groupName, Integer.toString(volume));
			} else {
				if (groupName.equals("master")) {
					int volume = Integer.parseInt(volumeString);
					ClientSingletonRepository.getSound().changeVolume(Numeric.intToFloat(volume, 100.0f));
					WtWindowManager.getInstance().setProperty("sound.volume." + groupName, Integer.toString(volume));
				} else {
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("No sound group \"" + groupName + "\" does exist"));
					ClientSingletonRepository.getUserInterface().addEventLine(new StandardEventLine("Please type \"/volume show\" for a valid list of groups"));
				}
			}
		} catch (NumberFormatException exception) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(volumeString + " is not a valid number", NotificationType.ERROR));
		}
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
