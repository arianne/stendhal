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

import games.stendhal.client.gui.j2DClient;

public class ClearChatLogAction implements SlashAction {

	public boolean execute(final String[] params, final String remainder) {
		((j2DClient) j2DClient.get()).clearGameLog();
		return true;
	}

	public int getMaximumParameters() {
		return 0;
	}

	public int getMinimumParameters() {
		return 0;
	}

}
