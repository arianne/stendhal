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
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * This action is executed after a successful transition of the state machine.
 *
 * @author hendrik
 */
public interface PostTransitionAction {

	/**
	 * does some action after a transition.
	 *
	 * @param player
	 *            player who caused the transition
	 * @param sentence
	 *            text he/she said
	 * @param raiser
	 *            the NPC doing the transition
	 */
	void fire(Player player, Sentence sentence, EventRaiser raiser);
}
