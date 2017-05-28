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
package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.piedpiper.ITPPQuestConstants.TPP_Phase;

public class TPPQuestInPhaseCondition implements ChatCondition {

	private TPP_Phase phase;

	public TPPQuestInPhaseCondition(TPP_Phase ph) {
		phase = ph;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		if(ThePiedPiper.getPhase().compareTo(phase)==0) {
			return true;
		}
		return false;
	}
}
