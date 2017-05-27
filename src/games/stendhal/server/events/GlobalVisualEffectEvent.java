/***************************************************************************
 *                 (C) Copyright 2003-2015 - Faiumoni e.V.                 *
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
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * An event that tells the client to display a visual effect that affects the
 * entire game screen.
 */
public class GlobalVisualEffectEvent extends RPEvent {
	/** For determining the effect to show. */
	private static final String NAME_ATTR = "effect_name";
	/** Duration in milliseconds. The exact interpretation is effect dependent. */
	private static final String DURATION_ATTR = "duration";
	/**
	 * Strength of the effect 1-100. The interpretation is effect dependent and
	 * may be ignored if it is not relevent.
	 */
	private static final String STRENGTH_ATTR = "strength";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.GLOBAL_VISUAL);
		rpclass.addAttribute(NAME_ATTR, Type.STRING);
		rpclass.addAttribute(DURATION_ATTR, Type.INT);
		rpclass.addAttribute(STRENGTH_ATTR, Type.INT);
	}

	/**
	 * Create a new GlobalVisualEffectEvent.
	 *
	 * @param effectName name of the effect
	 * @param duration Duration in milliseconds. The exact interpretation is
	 *  effect dependent
	 */
	public GlobalVisualEffectEvent(String effectName, int duration) {
		super(Events.GLOBAL_VISUAL);
		put(NAME_ATTR, effectName);
		put(DURATION_ATTR, duration);
	}

	/**
	 * Create a new GlobalVisualEffectEvent with a specified strength.
	 *
	 * @param effectName name of the effect
	 * @param duration Duration in milliseconds. The exact interpretation is
	 *  effect dependent
	 * @param strength strength of the effect. The meaning is effect dependent
	 */
	public GlobalVisualEffectEvent(String effectName, int duration, int strength) {
		super(Events.GLOBAL_VISUAL);
		put(NAME_ATTR, effectName);
		put(DURATION_ATTR, duration);
		put(STRENGTH_ATTR, strength);
	}
}
