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
package games.stendhal.server.entity.npc.fsm;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

/**
 * a condition to check before an transition is executed.
 *
 * @author hendrik
 */
public interface PreTransitionCondition {

	/**
	 * can the transition be done?
	 *
	 * @param player
	 *            player who caused the transition
	 * @param sentence
	 *            text he/she said
	 * @param entity
	 *            the NPC doing the transition
	 * @return true, if the transition is possible, false otherwise
	 */
	boolean fire(Player player, Sentence sentence, Entity entity);
}
