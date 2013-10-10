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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.journal.ProducerRegister;
import games.stendhal.server.entity.player.Player;

/**
 * List details about a produced item
 *
 * @author kymara
 */
@Dev(category=Category.ITEMS_PRODUCER, label="List")
public class ListProducedItemDetailAction implements ChatAction {

	private final ProducerRegister producerRegister = SingletonRepository.getProducerRegister();


	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		String itemName = sentence.getTriggerExpression().toString();
		String message = producerRegister.getProducedItemDetails(itemName);
		raiser.say(message);
	}

	@Override
	public String toString() {
		return "ListProducedItemDetailAction";
	}

	@Override
	public int hashCode() {
		return 5297;
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof ListProducedItemDetailAction);
	}

}
