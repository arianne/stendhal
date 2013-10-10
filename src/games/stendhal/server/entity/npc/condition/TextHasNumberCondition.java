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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Does this trigger contain a number?
 */
@Dev(category=Category.CHAT, label="\"\"?")
public class TextHasNumberCondition implements ChatCondition {
	private final int min;
	private final int max;

	/**
	 * Creates a new TextHasNumberCondition which checks for a positive integer.
	 */
	public TextHasNumberCondition() {
		this.min = 0;
		this.max = Integer.MAX_VALUE;
	}

	/**
	 * Creates a new TextHasNumberCondition which checks for a positive integer.
	 *
	 * @param min minimal accepted number
	 */
	public TextHasNumberCondition(final int min) {
		this.min = min;
		this.max = Integer.MAX_VALUE;
	}

	/**
	 * Creates a new TextHasNumberCondition which checks if there is a number
	 * and if it is in range.
	 *
	 * @param min minimal accepted number
	 * @param max maximal accepted number
	 */
	@Dev
	public TextHasNumberCondition(@Dev(defaultValue="0") final int min, @Dev(defaultValue="2147483647") final int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final Expression number = sentence.getNumeral();

		if (number != null) {
			final int num = number.getAmount();
			if ((num >= min) && (num <= max)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "has number<" + min + ", " + max + ">";
	}

	@Override
	public int hashCode() {
		return 47143 * min + max;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TextHasNumberCondition)) {
			return false;
		}
		TextHasNumberCondition other = (TextHasNumberCondition) obj;
		return (min == other.min)
			&& (max == other.max);
	}
}
