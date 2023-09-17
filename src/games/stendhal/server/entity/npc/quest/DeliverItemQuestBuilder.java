/***************************************************************************
 *                 (C) Copyright 2022-2023 - Faiumoni e.V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.Map;

import games.stendhal.common.grammar.Grammar;

public class DeliverItemQuestBuilder extends QuestBuilder<DeliverItemTask, DeliverItemQuestOfferBuilder, DeliverItemQuestCompleteBuilder, DeliverItemQuestHistoryBuilder> {

	public DeliverItemQuestBuilder() {
		super(new DeliverItemTask());
		offer = new DeliverItemQuestOfferBuilder();
		complete = new DeliverItemQuestCompleteBuilder(task());
		history = new DeliverItemQuestHistoryBuilder();
	}

	@Override
	protected void setupSimulator(QuestSimulator simulator) {
		super.setupSimulator(simulator);
		Map<String, DeliverItemOrder> orders = task().getOrders();
		String name = orders.keySet().iterator().next();
		DeliverItemOrder order = orders.get(name);

		simulator.setParam("flavor", order.getFlavor());
		simulator.setParam("customerName", Grammar.quoteHash("#" + name));
		simulator.setParam("time", Grammar.quantityplnoun(order.getExpectedMinutes(), "minute", "one"));
	}

}
