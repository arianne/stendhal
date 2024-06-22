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
package games.stendhal.server.events;

import games.stendhal.common.constants.Events;
import games.stendhal.server.core.engine.RPClassGenerator.RPClassWrapper;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPEvent;


/**
 * A generic event to be interpreted by client.
 */
public class GenericEvent extends RPEvent {

	private static final String ATTR_SUBEVENT = "subevent";
	private static final String ATTR_FLAGS = "flags";


	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClassWrapper wrapper = new RPClassWrapper(Events.GENERIC);

		// DEBUG: throw error
		/*
		System.out.println("\n\ncreating RPClass with attribute \"foo_attribute\"\n\n");
		wrapper.addAttribute("foo_attribute", Type.STRING, (byte) (Definition.PRIVATE|Definition.VOLATILE));
		*/

		wrapper.addAttribute(ATTR_SUBEVENT, Type.STRING, Definition.PRIVATE);
		wrapper.addAttribute(ATTR_FLAGS, Type.STRING, Definition.PRIVATE);
	}

	/**
	 * Creates a new generic event.
	 *
	 * @param subevent
	 *   Sub event identifier that client should execute.
	 */
	public GenericEvent(final String subevent) {
		super(Events.GENERIC);
		put(ATTR_SUBEVENT, subevent);
	}

	/**
	 * Creates a new generic event.
	 *
	 * @param subevent
	 *   Sub event identifier that client should execute.
	 * @param flags
	 *   Comma-separated string of flags to pass to client.
	 */
	public GenericEvent(final String subevent, final String flags) {
		this(subevent);
		put(ATTR_FLAGS, flags);
	}
}
