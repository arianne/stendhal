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

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.common.parser.TriggerList;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.Player;

/**
 * Was a trigger phrase said, which is a produced item of this class? (Use with a ""-trigger in npc.add)
 */
@Dev(category=Category.ITEMS_PRODUCER, label="Item?")
public class TriggerIsProducedItemOfClassCondition implements ChatCondition {

	private final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();

	private final String clazz;

	/**
	 * Creates a new TriggerIsProducedItemOfClassCondition.
	 *
	 * @param clazz
	 *            produced item class to check for
	 */
	public TriggerIsProducedItemOfClassCondition(final String clazz) {
		this.clazz = checkNotNull(clazz);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		TriggerList triggers = new TriggerList (producerRegister.getProducedItemNames(clazz));
		return triggers.contains(sentence.getTriggerExpression());
	}

	@Override
	public String toString() {
		return "trigger is produced item of class <" + clazz + ">";
	}

	@Override
	public int hashCode() {
		return 5039 * clazz.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof TriggerIsProducedItemOfClassCondition)) {
			return false;
		}
		TriggerIsProducedItemOfClassCondition other = (TriggerIsProducedItemOfClassCondition) obj;
		return clazz.equals(other.clazz);
	}
}
