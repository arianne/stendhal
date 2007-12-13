/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.buddy;

import games.stendhal.server.actions.CommandCenter;

public class BuddyAction  {

	private static final String _GRUMPY = "grumpy";
	private static final String _UNIGNORE = "unignore";
	private static final String _REMOVEBUDDY = "removebuddy";
	private static final String _IGNORE = "ignore";
	private static final String _ADDBUDDY = "addbuddy";

	public static void register() {
		CommandCenter.register(_ADDBUDDY, new AddBuddyAction());
		CommandCenter.register(_IGNORE, new IgnoreAction());
		CommandCenter.register(_REMOVEBUDDY, new RemoveBuddyAction());
		CommandCenter.register(_UNIGNORE, new UnignoreAction());
		CommandCenter.register(_GRUMPY, new GrumpyAction());
	}



}
