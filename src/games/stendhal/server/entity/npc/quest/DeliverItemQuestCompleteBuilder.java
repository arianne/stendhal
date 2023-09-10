/***************************************************************************
 *                 (C) Copyright 2003-2023 - Faiumoni e.V.                 *
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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.player.Player;

/**
 * defines how the NPC react after the player completes the quest
 *
 * @author hendrik
 */
class DeliverItemQuestCompleteBuilder extends QuestCompleteBuilder {

	private static Logger logger = Logger.getLogger(DeliverItemQuestCompleteBuilder.class);

	private DeliverItemTask deliverItemTask;

	public DeliverItemQuestCompleteBuilder(DeliverItemTask deliverItemTask) {
		this.deliverItemTask = deliverItemTask;
	}

	private static class HandOverItemAction implements ChatAction {
		private final DeliverItemTask deliverItemTask;
		private final String questSlot;

		public HandOverItemAction(DeliverItemTask deliverItemTask, String questSlot) {
			this.deliverItemTask = deliverItemTask;
			this.questSlot= questSlot; 
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			if (player.isEquipped("pizza")) {
				final DeliverItemOrder data = deliverItemTask.getOrders().get(npc.getName());
				for (final Item pizza : player.getAllEquipped("pizza")) {
					final String flavor = pizza.getInfoString();
					if (data.getFlavor().equals(flavor)) {
						player.drop(pizza);
						// Check whether the player was supposed to deliver the
						// pizza.
						if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
							if (deliverItemTask.isDeliveryTooLate(player)) {
								if (data.getMessageOnColdPizza().contains("%s")) {
									npc.say(String.format(data.getMessageOnColdPizza(), data.getFlavor()));
								} else {
									npc.say(data.getMessageOnColdPizza());
								}
								player.addXP(data.getXp() / 2);
							} else {
								if (data.getMessageOnHotPizza().contains("%s")) {
									npc.say(String.format(data.getMessageOnHotPizza(),
											data.getFlavor(), data.getTip()));
								} else {
									npc.say(String.format(data.getMessageOnHotPizza(),
											data.getTip()));
								}
								final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
								money.setQuantity(data.getTip());
								player.equipOrPutOnGround(money);
								player.addXP(data.getXp());
								player.addKarma(5);
							}
							new InflictStatusOnNPCAction("pizza").fire(player, null, npc);
							player.setQuest(questSlot, "done");
							new SetQuestToTimeStampAction(questSlot, 1).fire(player, null, npc);
							new IncrementQuestAction(questSlot, 2, 1).fire(player, null, npc);
							deliverItemTask.putOffUniform(player);
						} else {
							// This should not happen: a player cannot pick up a pizza from the ground
							// that did have a flavor, those are bound. If a pizza has flavor the player
							// should only have got it from the quest.
							npc.say("Eek! This pizza is all dirty! Did you find it on the ground?");
						}
						return;
					}
				}
				// The player has brought the pizza to the wrong NPC, or it's a plain pizza.
				npc.say("No, thanks. I like " + data.getFlavor() + " better.");
			} else {
				npc.say("A pizza? Where?");
			}
		}
	}

	@Override
	void simulate(String npc, QuestSimulator simulator) {
		// TODO
	}

	@Override
	void build(SpeakerNPC mainNpc, String questSlot, ChatCondition questCompletedCondition, ChatAction questCompleteAction) {
		ChatAction handOverItemAction = new HandOverItemAction(this.deliverItemTask, questSlot);
		for (final String name : deliverItemTask.getOrders().keySet()) {
			final SpeakerNPC npc = NPCList.get().get(name);
			if (npc == null) {
				logger.error("NPC " + name + " is used in the Pizza Delivery quest but does not exist in game.", new Throwable());
				continue;
			}

			npc.add(ConversationStates.ATTENDING, "pizza", null,
				ConversationStates.ATTENDING, null,
				handOverItemAction);
		}
	}


}
