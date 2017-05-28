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
package games.stendhal.server.events;

import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * A graphvis diagram showing the FSM of an NPC
 *
 * @author hendrik
 */
public class TransitionGraphEvent extends RPEvent {

	private static final String TRANSITION_GRAPH = "transition_graph";
	private static final String DATA = "data";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(TRANSITION_GRAPH);
		rpclass.addAttribute(DATA, Type.LONG_STRING);
	}

	/**
	 * Creates a new TransitionGraphEvent.
	 *
	 * @param data data to display
	 */
	public TransitionGraphEvent(String data) {
		super(TRANSITION_GRAPH);
		put(DATA, data);
	}
}
