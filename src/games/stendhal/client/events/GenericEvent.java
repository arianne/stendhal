/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.events.generic.SubEvent;


/**
 * A generic event that will execute a registered sub-event.
 *
 * TODO: allow execution without an associated entity
 */
public class GenericEvent extends Event<Entity> {

	/** Registered sub-events. */
	private static Map<String, Class<? extends SubEvent>> registry = new HashMap<String,
			Class<? extends SubEvent>>();


	@Override
	public void execute() {
		final String subevent = event.get("subevent");
		if (subevent == null || !registry.containsKey(subevent)) {
			Logger.getLogger(GenericEvent.class).warn("Unknown generic event: " + subevent);
			return;
		}
		final String[] flags = event.has("flags") ? event.get("flags").split(",") : new String[] {};
		try {
			final Constructor<? extends SubEvent> constructor = registry.get(subevent)
					.getConstructor(String[].class);
			constructor.newInstance((Object) flags).execute(entity);
		} catch (NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException
				|IllegalArgumentException|InvocationTargetException e) {
			Logger.getLogger(GenericEvent.class).error("Failed to create sub-event instance", e);
		}
	}
}
