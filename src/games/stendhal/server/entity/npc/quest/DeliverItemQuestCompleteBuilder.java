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

import java.util.HashMap;
import java.util.Map;

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
import games.stendhal.server.util.StringUtils;

/**
 * defines how the NPC react after the player completes the quest
 *
 * @author hendrik
 */
public class DeliverItemQuestCompleteBuilder extends QuestCompleteBuilder {

	private static Logger logger = Logger.getLogger(DeliverItemQuestCompleteBuilder.class);

	private DeliverItemTask deliverItemTask;
	private String respondToItemWithoutQuest;
	private String respondToItemForOtherNPC;
	private String respondToMissingItem;
	private String npcStatusEffect;

	DeliverItemQuestCompleteBuilder(DeliverItemTask deliverItemTask) {
		this.deliverItemTask = deliverItemTask;
	}

	private class HandOverItemAction implements ChatAction {
		private final DeliverItemTask deliverItemTask;
		private final String questSlot;

		public HandOverItemAction(DeliverItemTask deliverItemTask, String questSlot) {
			this.deliverItemTask = deliverItemTask;
			this.questSlot= questSlot; 
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			if (player.isEquipped(deliverItemTask.getItemName())) {
				final DeliverItemOrder data = deliverItemTask.getOrders().get(npc.getName());
				Map<String, Object> params = new HashMap<>();
				params.put("flavor", data.getFlavor());
				params.put("tip", data.getTip());
				for (final Item item : player.getAllEquipped(deliverItemTask.getItemName())) {
					final String flavor = item.getInfoString();
					if (data.getFlavor().equals(flavor)) {
						player.drop(item);
						// Check whether the player was supposed to deliver this item.
						if (player.hasQuest(questSlot) && !player.isQuestCompleted(questSlot)) {
							if (deliverItemTask.isDeliveryTooLate(player, questSlot)) {
								npc.say(StringUtils.substitute(data.getRespondToSlowDelivery(), params));
								player.addXP(data.getXp() / 2);
							} else {
								npc.say(StringUtils.substitute(data.getRespondToFastDelivery(), params));
								final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
								money.setQuantity(data.getTip());
								player.equipOrPutOnGround(money);
								player.addXP(data.getXp());
								player.addKarma(5);
							}
							if (npcStatusEffect != null) {
								new InflictStatusOnNPCAction(npcStatusEffect).fire(player, null, npc);
							}
							player.setQuest(questSlot, 0, "done");
							new SetQuestToTimeStampAction(questSlot, 1).fire(player, null, npc);
							new IncrementQuestAction(questSlot, 2, 1).fire(player, null, npc);
							deliverItemTask.putOffUniform(player);
						} else {
							// Item could be from a previous failed attempt to do this quest.
							npc.say(respondToItemWithoutQuest);
						}
						return;
					}
				}
				// The player has brought the item to the wrong NPC, or it's a plain item.
				npc.say(StringUtils.substitute(respondToItemForOtherNPC, params));

			} else {
				npc.say(respondToMissingItem);
			}
		}
	}


	public DeliverItemQuestCompleteBuilder respondToItemWithoutQuest(String respondToItemWithoutQuest) {
		this.respondToItemWithoutQuest = respondToItemWithoutQuest;
		return this;
	}

	public DeliverItemQuestCompleteBuilder respondToItemForOtherNPC(String respondToItemForOtherNPC) {
		this.respondToItemForOtherNPC = respondToItemForOtherNPC;
		return this;
	}

	public DeliverItemQuestCompleteBuilder respondToMissingItem(String respondToMissingItem) {
		this.respondToMissingItem = respondToMissingItem;
		return this;
	}

	public DeliverItemQuestCompleteBuilder npcStatusEffect(String npcStatusEffect) {
		this.npcStatusEffect = npcStatusEffect;
		return this;
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
				logger.error("NPC " + name + " is used in the DeliveryItemQuest " + questSlot + " but they do not exist in game.", new Throwable());
				continue;
			}

			npc.add(ConversationStates.ATTENDING, deliverItemTask.getItemName(), null,
				ConversationStates.ATTENDING, null,
				handOverItemAction);
		}
	}


}
