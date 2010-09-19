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
package games.stendhal.client.events;

import games.stendhal.client.entity.Entity;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;

/**
 * dispatches all events to the appropriate event handler class.
 *
 * @author hendrik
 */
public class EventDispatcher {

	/**
	 * dispatches events
	 *
	 * @param rpobject RPObject with events
	 * @param entity Entity
	 */
	public static void dispatchEvents(final RPObject rpobject, Entity entity) {
		for (final RPEvent rpevent : rpobject.events()) {
			Event<? extends Entity> event = EventFactory.create(entity, rpevent);
			event.execute();
		}
	}
}
