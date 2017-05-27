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
package games.stendhal.server.core.rp.achievement.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Are all the quests in this region completed?
 */
public class QuestsInRegionCompletedCondition implements ChatCondition {

	private final String region;

	/**
	 * Creates a new QuestsInRegionCompletedCondition.
	 *
	 * @param region
	 *            name of Region to check
	 */
	public QuestsInRegionCompletedCondition(final String region) {
		this.region = checkNotNull(region);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		List<String> quests = SingletonRepository.getStendhalQuestSystem().getIncompleteQuests(player, region);
		return quests.isEmpty();
	}

	@Override
	public String toString() {
		return "QuestsInRegionCompleted <" + region + ">";
	}

	@Override
	public int hashCode() {
		return 47 * region.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof QuestsInRegionCompletedCondition)) {
			return false;
		}
		return region.equals(((QuestsInRegionCompletedCondition) obj).region);
	}
}
