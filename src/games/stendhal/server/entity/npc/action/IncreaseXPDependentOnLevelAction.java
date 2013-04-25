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
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.Level;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Increases the xp of the current player.
 */
@Dev(category=Category.STATS, label="XP+")
public class IncreaseXPDependentOnLevelAction implements ChatAction {
    // player will get 1/xpDiff part of difference between his and next levels xp amount.
	private final double xpDiff;
	// player will get this bonus instead xp if have max level
	private final double karmabonus;

	/**
	 * Creates a new IncreaseXPDependentOnLevelAction.
	 *
	 * @param xpDiff - player will get 1/xpDiff of difference between his and next levels xp amount.
	 * @param karmabonus - amount of karma to add instead xp if player have max level
	 */
	public IncreaseXPDependentOnLevelAction(final double xpDiff, final double karmabonus) {
		this.xpDiff = xpDiff;
		this.karmabonus = karmabonus;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		final int start = Level.getXP(player.getLevel());
		final int next = Level.getXP(player.getLevel() + 1);
		int reward = (int) ((next - start) / xpDiff);
		if (player.getLevel() >= Level.maxLevel()) {
			reward = 0;
			// no reward so give a lot karma instead
			player.addKarma(karmabonus);
		}
		player.addXP(reward);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "IncreaseXPDependentOnLevel <" + xpDiff + ","+karmabonus+">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (int)(xpDiff);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final IncreaseXPDependentOnLevelAction other = (IncreaseXPDependentOnLevelAction) obj;
		if (xpDiff != other.xpDiff) {
			return false;
		}
		return true;
	}

}
