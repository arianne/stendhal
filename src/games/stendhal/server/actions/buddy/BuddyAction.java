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

	public static void register() {
		CommandCenter.register("addbuddy", new AddBuddyAction());
		CommandCenter.register("ignore", new IgnoreAction());
		CommandCenter.register("removebuddy", new RemoveBuddyAction());
		CommandCenter.register("unignore", new UnignoreAction());
		CommandCenter.register("grumpy", new GrumpyAction());
	}



}
