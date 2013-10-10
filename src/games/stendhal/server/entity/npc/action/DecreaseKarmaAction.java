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
 * Decreases the karma of the current player.
 */
@Dev(category=Category.STATS, label="Karma-")
public class DecreaseKarmaAction implements ChatAction {

	private final double karmaDiff;

	/**
	 * Creates a new DecreaseKarmaAction.
	 *
	 * @param karmaDiff
	 *            amount of karma to subtract
	 */
	public DecreaseKarmaAction(final double karmaDiff) {
		this.karmaDiff = karmaDiff;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		player.addKarma(-1 * karmaDiff);
	}

	@Override
	public String toString() {
		return "DecreaseKarma<" + karmaDiff + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(karmaDiff);
		result = PRIME * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DecreaseKarmaAction)) {
			return false;
		}
		DecreaseKarmaAction other = (DecreaseKarmaAction) obj;
		return (Double.doubleToLongBits(karmaDiff) == Double.doubleToLongBits(other.karmaDiff));
	}
}
