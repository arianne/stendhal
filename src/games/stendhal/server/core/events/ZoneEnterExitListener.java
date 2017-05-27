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
package games.stendhal.server.core.events;

import games.stendhal.server.core.engine.StendhalRPZone;
import marauroa.common.game.RPObject;

public interface ZoneEnterExitListener {
	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param object
	 *            The object that entered.
	 * @param zone
	 *            The new zone.
	 */
	void onEntered(RPObject object, StendhalRPZone zone);

	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param object
	 *            The object that exited.
	 * @param zone
	 *            The zone that was exited.
	 *
	 */
	void onExited(RPObject object, StendhalRPZone zone);

}
