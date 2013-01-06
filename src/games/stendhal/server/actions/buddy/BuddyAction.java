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

import static games.stendhal.common.constants.Actions.ADDBUDDY;
import static games.stendhal.common.constants.Actions.GRUMPY;
import static games.stendhal.common.constants.Actions.IGNORE;
import static games.stendhal.common.constants.Actions.REMOVEBUDDY;
import static games.stendhal.common.constants.Actions.UNIGNORE;
import games.stendhal.server.actions.CommandCenter;
public class BuddyAction {


	public static void register() {
		CommandCenter.register(ADDBUDDY, new AddBuddyAction());
		CommandCenter.register(IGNORE, new IgnoreAction());
		CommandCenter.register(REMOVEBUDDY, new RemoveBuddyAction());
		CommandCenter.register(UNIGNORE, new UnignoreAction());
		CommandCenter.register(GRUMPY, new GrumpyAction());
	}

}
