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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.settings.SettingsDialog;

/**
 * Show the settings dialog
 */
class SettingsAction implements SlashAction {
	private SettingsDialog dialog;

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
	public boolean execute(String[] params, String remainder) {
		if (dialog == null) {
			dialog = new SettingsDialog(j2DClient.get().getMainFrame());
			dialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					dialog = null;
				}
			});
		}
		dialog.setVisible(true);
		dialog.toFront();

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
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
