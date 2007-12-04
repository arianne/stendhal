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

import games.stendhal.server.actions.CommandCentre;

public class BuddyAction  {

	public static void register() {
		CommandCentre.register("addbuddy", new AddBuddyAction());
		CommandCentre.register("ignore", new IgnoreAction());
		CommandCentre.register("removebuddy", new RemoveBuddyAction());
		CommandCentre.register("unignore", new UnignoreAction());
		CommandCentre.register("grumpy", new GrumpyAction());
	}



}
