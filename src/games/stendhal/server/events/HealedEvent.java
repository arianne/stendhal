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

import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * A healed event.
 *
 * @author hendrik
 */
public class HealedEvent extends RPEvent {
	private static final String RPCLASS_NAME = "healed";
	private static final String AMOUNT = "amount";

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(RPCLASS_NAME);
		rpclass.add(DefinitionClass.ATTRIBUTE, AMOUNT, Type.INT);
	}

	/**
	 * Creates a new healed event.
	 *
	 * @param amount amount of hp healed
	 */
	public HealedEvent(final int amount) {
		super(RPCLASS_NAME);
		put(AMOUNT, amount);
	}
}
