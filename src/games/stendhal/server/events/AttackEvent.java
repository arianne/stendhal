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

import games.stendhal.common.constants.Nature;
import games.stendhal.common.constants.Events;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.Definition.Type;

/**
 * An RPEntity attacks another
 */
public class AttackEvent extends RPEvent {
	private static final String HIT_ATTR = "hit";
	private static final String DAMAGE_ATTR = "damage";
	private static final String DAMAGE_TYPE_ATTR = "type";
	
	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.ATTACK);
		rpclass.addAttribute(HIT_ATTR, Type.FLAG);
		rpclass.addAttribute(DAMAGE_ATTR, Type.INT);
		rpclass.addAttribute(DAMAGE_TYPE_ATTR, Type.INT);
	}

	/**
	 * Construct a new <code>AttackEvent</code>
	 * 
	 * @param canHit <code>false</code> for missed hits, <code>true</code> for wounding or blocked hits
	 * @param damage damage done
	 * @param type damage type of the attack
	 */
	public AttackEvent(boolean canHit, int damage, Nature type) {
		super(Events.ATTACK);
		if (canHit) {
			put(HIT_ATTR, "");
		}
		put(DAMAGE_ATTR, damage);
		put(DAMAGE_TYPE_ATTR, type.ordinal());
	}
}
