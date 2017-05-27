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

import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.TriggerList;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Was one of theses trigger phrases said? (Use with a ""-trigger in npc.add)
 */
@Dev(category=Category.CHAT, label="\"\"?")
public class TriggerInListCondition implements ChatCondition {
	private final TriggerList triggers;

	/**
	 * Creates a new TriggerInListCondition.
	 *
	 * @param trigger
	 *            list of trigger
	 */
	public TriggerInListCondition(final String... trigger) {
		this(Arrays.asList(trigger));
	}

	/**
	 * Creates a new TriggerInListCondition.
	 *
	 * @param trigger
	 *            list of trigger
	 */
	@Dev
	public TriggerInListCondition(final List<String> trigger) {
		triggers = new TriggerList(trigger);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return triggers.contains(sentence.getTriggerExpression());
	}

	@Override
	public String toString() {
		return "trigger <" + triggers.toString() + ">";
	}

	@Override
	public int hashCode() {
		return 5021 * triggers.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TriggerInListCondition)) {
			return false;
		}
		TriggerInListCondition other = (TriggerInListCondition) obj;
		return triggers.equals(other.triggers);
	}
}
