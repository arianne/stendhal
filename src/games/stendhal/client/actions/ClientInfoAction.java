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
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * Send a support request message with client information.
 */
class ClientInfoAction implements SlashAction {
	private boolean first = true;

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		final RPAction tell = new RPAction();

		StringBuilder sb = new StringBuilder();
		String patchLevel = System.getProperty("sun.os.patch.level");
		if ((patchLevel == null) || (patchLevel.equals("unknown"))) {
			patchLevel = "";
		}

		sb.append("\n");
		sb.append("Stendhal: " + Debug.VERSION);
		if (Debug.PRE_RELEASE_VERSION != null) {
			sb.append(" - ");
			sb.append(Debug.PRE_RELEASE_VERSION);
		}
		sb.append("\n");
		// TODO: add classpath and information about webstart/download
		sb.append("OS: " + System.getProperty("os.name") + " " + patchLevel
				+ " " + System.getProperty("os.version") + " "
				+ System.getProperty("os.arch") + "\n");
		sb.append("Java-Runtime: " + System.getProperty("java.runtime.name")
				+ " " + System.getProperty("java.runtime.version") + " from "
				+ System.getProperty("java.home") + "\n");
		sb.append("Java-VM: " + System.getProperty("java.vm.vendor") + " "
				+ System.getProperty("java.vm.name") + " "
				+ System.getProperty("java.vm.version") + "\n");

		final long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
		final long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
		sb.append("Total/Used memory: " + totalMemory + "/" + (totalMemory - freeMemory) + "\n");
		ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(sb.toString(), NotificationType.CLIENT));

		if (first) {
			tell.put("type", "support");
			tell.put("text", sb.toString());
			ClientSingletonRepository.getClientFramework().send(tell);
			first = false;
		}

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
