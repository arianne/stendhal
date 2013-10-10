/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.Behaviour;
import games.stendhal.server.entity.player.Player;

/**
 * BehaviourAction handles Behaviour requests.
 */
@Dev(category=Category.IGNORE)
public abstract class BehaviourAction extends AbstractBehaviourAction<Behaviour> {

	/**
	 * Behaviour action
	 *
	 * @param behaviour behaviour
	 * @param userAction user action
	 * @param npcAction npc action
	 */
	public BehaviourAction(final Behaviour behaviour, String userAction, String npcAction) {
		super(behaviour, userAction, npcAction);
	}

	@Override
	public void fireRequestError(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser npc) {
		npc.say(behaviour.getErrormessage(res, userAction, npcAction));
	}

	@Override
	public String toString() {
		return "BehaviourAction";
	}

}
