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
public class IncreaseXPAction implements ChatAction {

	private final int xpDiff;

	/**
	 * Creates a new IncreaseKarmaAction.
	 *
	 * @param xpDiff
	 *            amount of xp to add
	 */
	public IncreaseXPAction(final int xpDiff) {
		this.xpDiff = xpDiff;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		player.addXP(xpDiff);
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "IncreaseXP <" + xpDiff + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + xpDiff;
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
		final IncreaseXPAction other = (IncreaseXPAction) obj;
		if (xpDiff != other.xpDiff) {
			return false;
		}
		return true;
	}

}
